package com.unihub.app.features.location.infrastructure.data.local.datasource

import com.unihub.app.features.location.infrastructure.data.local.dao.LocationDao
import com.unihub.app.features.location.infrastructure.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationLocalDataSource @Inject constructor(
    private val locationDao: LocationDao
) {
    fun getLocations(userId: String): Flow<List<LocationEntity>> =
        locationDao.getLocations(userId)

    fun getLocationById(id: String): Flow<LocationEntity?> =
        locationDao.getLocationById(id)

    suspend fun insertLocation(location: LocationEntity) =
        locationDao.insertLocation(location)

    suspend fun deleteLocation(id: String) =
        locationDao.deleteLocation(id)
}
