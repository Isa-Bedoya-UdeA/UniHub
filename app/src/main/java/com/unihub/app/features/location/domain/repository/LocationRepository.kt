package com.unihub.app.features.location.domain.repository

import com.unihub.app.features.location.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocations(userId: String): Flow<List<Location>>
    fun getLocationById(id: String): Flow<Location?>
    suspend fun saveLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun deleteLocation(id: String)
}
