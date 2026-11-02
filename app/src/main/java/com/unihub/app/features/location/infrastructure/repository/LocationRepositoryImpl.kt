package com.unihub.app.features.location.infrastructure.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.domain.repository.Coordinates
import com.unihub.app.features.location.domain.repository.LocationCandidate
import com.unihub.app.features.location.domain.repository.LocationRepository
import com.unihub.app.features.location.infrastructure.data.local.datasource.LocationLocalDataSource
import com.unihub.app.features.location.infrastructure.data.mapper.toDomain
import com.unihub.app.features.location.infrastructure.data.mapper.toDto
import com.unihub.app.features.location.infrastructure.data.mapper.toEntity
import com.unihub.app.features.location.infrastructure.data.remote.datasource.LocationRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localDataSource: LocationLocalDataSource,
    private val remoteDataSource: LocationRemoteDataSource,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val placesClient: PlacesClient?
) : LocationRepository {

    private var currentSessionToken: AutocompleteSessionToken? = null

    private val placeFields = listOf(
        Place.Field.ID,
        Place.Field.DISPLAY_NAME,
        Place.Field.FORMATTED_ADDRESS,
        Place.Field.LOCATION
    )

    override fun getLocations(userId: String): Flow<List<Location>> {
        return localDataSource.getLocations(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLocationById(id: String): Flow<Location?> {
        return localDataSource.getLocationById(id).map { it?.toDomain() }
    }

    override suspend fun saveLocation(location: Location) {
        localDataSource.insertLocation(location.toEntity())
        
        try {
            val dto = location.toDto()
            remoteDataSource.saveLocation(location.userId, dto)
        } catch (e: Exception) {
            Log.e("LocationRepositoryImpl", "Error saving location to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteLocation(id: String) {
        val location = localDataSource.getLocationById(id).firstOrNull()
        localDataSource.deleteLocation(id)
        location?.let {
            try {
                remoteDataSource.deleteLocation(it.userId, id)
            } catch (e: Exception) {
                Log.e("LocationRepositoryImpl", "Error deleting location from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun searchPlaces(query: String, locationBias: Coordinates?): List<LocationCandidate> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return emptyList()

        // 1. Intentar primero con PlacesClient si está disponible
        if (placesClient != null) {
            try {
                val token = AutocompleteSessionToken.newInstance()
                currentSessionToken = token

                val requestBuilder = FindAutocompletePredictionsRequest.builder()
                    .setQuery(trimmedQuery)
                    .setSessionToken(token)
                    .setRegionCode("CO")

                if (locationBias != null && locationBias.latitude != 0.0 && locationBias.longitude != 0.0) {
                    try {
                        val center = com.google.android.gms.maps.model.LatLng(locationBias.latitude, locationBias.longitude)
                        val circularBounds = CircularBounds.newInstance(center, 25000.0)
                        requestBuilder.setLocationBias(circularBounds)
                    } catch (t: Throwable) {
                        Log.w("LocationRepo", "Could not set location bias: ${t.message}")
                    }
                }

                val response = placesClient.findAutocompletePredictions(requestBuilder.build()).await()
                val predictions = response.autocompletePredictions.map { prediction ->
                    val name = prediction.getPrimaryText(null)?.toString()?.takeIf { it.isNotBlank() }
                        ?: prediction.getFullText(null)?.toString()
                        ?: trimmedQuery
                    val address = prediction.getSecondaryText(null)?.toString() ?: ""
                    LocationCandidate(
                        name = name,
                        address = address,
                        latitude = 0.0,
                        longitude = 0.0,
                        placeId = prediction.placeId
                    )
                }

                if (predictions.isNotEmpty()) {
                    return predictions
                }
            } catch (e: ApiException) {
                Log.w("LocationRepo", "Places API error: ${e.statusCode} - ${e.message}. Falling back to Geocoder.")
            } catch (t: Throwable) {
                Log.w("LocationRepo", "Places SDK error: ${t.message}. Falling back to Geocoder.")
            }
        }

        // 2. Fallback robusto nativo con Geocoder de Android (funciona siempre, sin cuotas ni claves de Places)
        return searchWithGeocoder(trimmedQuery)
    }

    private suspend fun searchWithGeocoder(query: String): List<LocationCandidate> = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) return@withContext emptyList()
            val geocoder = Geocoder(context, Locale.getDefault())

            val addresses: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    try {
                        geocoder.getFromLocationName(query, 5, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                if (continuation.isActive) {
                                    continuation.resume(addresses)
                                }
                            }
                            override fun onError(errorMessage: String?) {
                                Log.w("LocationRepo", "Geocoder error: $errorMessage")
                                if (continuation.isActive) {
                                    continuation.resume(emptyList())
                                }
                            }
                        })
                    } catch (t: Throwable) {
                        if (continuation.isActive) {
                            continuation.resume(emptyList())
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                try {
                    geocoder.getFromLocationName(query, 5) ?: emptyList()
                } catch (t: Throwable) {
                    emptyList()
                }
            }

            addresses.map { address ->
                val name = address.featureName?.takeIf { it.isNotBlank() }
                    ?: address.subLocality
                    ?: address.thoroughfare
                    ?: address.locality
                    ?: query
                val fullAddress = (0..address.maxAddressLineIndex)
                    .mapNotNull { address.getAddressLine(it) }
                    .joinToString(", ")
                    .ifBlank {
                        listOfNotNull(address.subLocality, address.locality, address.adminArea, address.countryName)
                            .joinToString(", ")
                    }

                LocationCandidate(
                    name = name,
                    address = fullAddress,
                    latitude = address.latitude,
                    longitude = address.longitude,
                    placeId = null // Coordenadas ya resueltas directamente
                )
            }
        } catch (t: Throwable) {
            Log.e("LocationRepo", "Geocoder fallback error: ${t.message}")
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Coordinates? {
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            location?.let {
                Coordinates(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPlaceDetails(placeId: String): LocationCandidate? {
        if (placesClient == null) return null
        return try {
            val requestBuilder = FetchPlaceRequest.builder(placeId, placeFields)

            currentSessionToken?.let { token ->
                requestBuilder.setSessionToken(token)
            }

            val response = placesClient.fetchPlace(requestBuilder.build()).await()
            val place = response.place

            currentSessionToken = null

            LocationCandidate(
                name = place.displayName ?: "Lugar",
                address = place.formattedAddress ?: "",
                latitude = place.location?.latitude ?: 0.0,
                longitude = place.location?.longitude ?: 0.0,
                placeId = place.id
            )
        } catch (e: ApiException) {
            Log.e("LocationRepo", "Error getting place details: ${e.statusCode} - ${e.message}")
            null
        } catch (t: Throwable) {
            Log.e("LocationRepo", "Error getting place details: ${t.message}")
            null
        }
    }

    override suspend fun syncLocations(userId: String) {
        try {
            val remoteLocations = remoteDataSource.getLocations(userId)
            remoteLocations.forEach { dto ->
                localDataSource.insertLocation(dto.toDomain().toEntity())
            }
        } catch (e: Exception) {
            Log.e("LocationRepositoryImpl", "Error syncing locations from Firestore: ${e.message}")
        }
    }
}
