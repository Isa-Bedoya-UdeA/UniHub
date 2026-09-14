package com.unihub.app.features.location.infrastructure.repository

import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.domain.repository.LocationRepository
import com.unihub.app.features.location.infrastructure.data.local.dao.LocationDao
import com.unihub.app.features.location.infrastructure.data.mapper.toDomain
import com.unihub.app.features.location.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {

    override fun getLocations(userId: String): Flow<List<Location>> =
        locationDao.getLocations(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getLocationById(id: String): Flow<Location?> =
        locationDao.getLocationById(id).map { it?.toDomain() }

    override suspend fun saveLocation(location: Location) {
        locationDao.insertLocation(location.toEntity())
    }

    override suspend fun updateLocation(location: Location) {
        locationDao.insertLocation(location.toEntity())
    }

    override suspend fun deleteLocation(id: String) {
        locationDao.deleteLocation(id)
    }
}
