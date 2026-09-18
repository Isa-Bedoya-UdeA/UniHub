package com.unihub.app.features.location.application.usecase

import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.domain.repository.Coordinates
import com.unihub.app.features.location.domain.repository.LocationCandidate
import com.unihub.app.features.location.domain.repository.LocationRepository
import javax.inject.Inject

class SearchLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(query: String, locationBias: Coordinates? = null): List<LocationCandidate> {
        if (query.isBlank()) return emptyList()
        return repository.searchPlaces(query, locationBias)
    }
}

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Coordinates? {
        return repository.getCurrentLocation()
    }
}

class SaveLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: Location) {
        repository.saveLocation(location)
    }
}

class GetPlaceDetailsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(placeId: String): LocationCandidate? {
        return repository.getPlaceDetails(placeId)
    }
}

class GetLocationByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    fun invoke(id: String) = repository.getLocationById(id)
}

class OpenExternalMapUseCase @Inject constructor() {
    operator fun invoke(context: android.content.Context, latitude: Double, longitude: Double, label: String? = null) {
        val uri = if (label != null) {
            "geo:$latitude,$longitude?q=$latitude,$longitude($label)"
        } else {
            "geo:$latitude,$longitude"
        }
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to any map app
            val genericIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
            context.startActivity(genericIntent)
        }
    }
}
