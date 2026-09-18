package com.unihub.app.features.location.domain.repository

import com.unihub.app.features.location.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface LocationRepository {
    fun getLocations(userId: String): Flow<List<Location>>
    fun getLocationById(id: String): Flow<Location?>
    suspend fun saveLocation(location: Location)
    suspend fun deleteLocation(id: String)
    
    // External services (simplified into repository for this scope)
    suspend fun searchPlaces(query: String, locationBias: Coordinates? = null): List<LocationCandidate>
    suspend fun getCurrentLocation(): Coordinates?
    suspend fun getPlaceDetails(placeId: String): LocationCandidate?
}

@Serializable
data class LocationCandidate(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String? = null
)

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
