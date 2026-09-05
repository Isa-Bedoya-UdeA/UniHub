package com.unihub.app.features.location.infrastructure.repository

import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor() : LocationRepository {

    private val mockLocations = MutableStateFlow<List<Location>>(emptyList())

    override fun getLocations(userId: String): Flow<List<Location>> =
        mockLocations.map { it.filter { l -> l.userId == userId } }

    override fun getLocationById(id: String): Flow<Location?> =
        mockLocations.map { it.find { l -> l.id == id } }

    override suspend fun saveLocation(location: Location) {
        val current = mockLocations.value.toMutableList()
        current.add(location)
        mockLocations.emit(current)
    }

    override suspend fun updateLocation(location: Location) {
        val current = mockLocations.value.toMutableList()
        val index = current.indexOfFirst { it.id == location.id }
        if (index != -1) {
            current[index] = location
            mockLocations.emit(current)
        }
    }

    override suspend fun deleteLocation(id: String) {
        mockLocations.emit(mockLocations.value.filter { it.id != id })
    }
}
