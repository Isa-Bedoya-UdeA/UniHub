package com.unihub.app.features.location.presentation.state

import com.unihub.app.features.location.domain.repository.Coordinates
import com.unihub.app.features.location.domain.repository.LocationCandidate

data class LocationSelectionState(
    val searchQuery: String = "",
    val searchResults: List<LocationCandidate> = emptyList(),
    val selectedLocation: LocationCandidate? = null,
    val currentDeviceLocation: Coordinates? = Coordinates(6.2442, -75.5812),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val isPermissionGranted: Boolean = false
)
