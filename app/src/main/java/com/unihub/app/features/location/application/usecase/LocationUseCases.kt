package com.unihub.app.features.location.application.usecase

import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.domain.repository.LocationRepository
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(private val repository: LocationRepository) {
    operator fun invoke(userId: String) = repository.getLocations(userId)
}

class GetLocationByIdUseCase @Inject constructor(private val repository: LocationRepository) {
    operator fun invoke(id: String) = repository.getLocationById(id)
}

class SaveLocationUseCase @Inject constructor(private val repository: LocationRepository) {
    suspend operator fun invoke(location: Location) = repository.saveLocation(location)
}

class UpdateLocationUseCase @Inject constructor(private val repository: LocationRepository) {
    suspend operator fun invoke(location: Location) = repository.updateLocation(location)
}

class DeleteLocationUseCase @Inject constructor(private val repository: LocationRepository) {
    suspend operator fun invoke(id: String) = repository.deleteLocation(id)
}
