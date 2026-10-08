package com.unihub.app.features.location.infrastructure.repository

import android.annotation.SuppressLint
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val localDataSource: LocationLocalDataSource,
    private val remoteDataSource: LocationRemoteDataSource,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val placesClient: PlacesClient
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
        currentSessionToken = AutocompleteSessionToken.newInstance()

        val requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(currentSessionToken)
            .setRegionCode("CO")

        locationBias?.let { bias ->
            val center = com.google.android.gms.maps.model.LatLng(bias.latitude, bias.longitude)
            val circularBounds = CircularBounds.newInstance(center, 50000.0)
            requestBuilder.setLocationBias(circularBounds)
        }

        return try {
            val response = placesClient.findAutocompletePredictions(requestBuilder.build()).await()
            response.autocompletePredictions.map { prediction ->
                LocationCandidate(
                    name = prediction.getPrimaryText(null).toString(),
                    address = prediction.getSecondaryText(null).toString(),
                    latitude = 0.0,
                    longitude = 0.0,
                    placeId = prediction.placeId
                )
            }
        } catch (e: ApiException) {
            Log.e("LocationRepo", "Places API error: ${e.statusCode} - ${e.message}")
            throw RuntimeException("Error en Places API: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("LocationRepo", "Error searching places", e)
            throw e
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
        } catch (e: Exception) {
            Log.e("LocationRepo", "Error getting place details", e)
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
