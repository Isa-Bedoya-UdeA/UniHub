package com.unihub.app.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.location.application.usecase.GetCurrentLocationUseCase
import com.unihub.app.features.location.application.usecase.GetPlaceDetailsUseCase
import com.unihub.app.features.location.application.usecase.SearchLocationsUseCase
import com.unihub.app.features.location.domain.repository.Coordinates
import com.unihub.app.features.location.domain.repository.LocationCandidate
import com.unihub.app.features.location.presentation.state.LocationSelectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationSelectionViewModel @Inject constructor(
    private val searchLocationsUseCase: SearchLocationsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LocationSelectionState())
    val state: StateFlow<LocationSelectionState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(300) // Debounce para evitar sobrecargar llamadas mientras se escribe
                searchLocations(trimmed, _state.value.currentDeviceLocation)
            }
        } else {
            _state.update { it.copy(searchResults = emptyList(), isSearching = false, errorMessage = null) }
        }
    }

    fun onSearchSubmit() {
        val query = _state.value.searchQuery.trim()
        if (query.length >= 3) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                searchLocations(query, _state.value.currentDeviceLocation)
            }
        }
    }

    fun updateMapCenter(coordinates: Coordinates) {
        _state.update { it.copy(currentDeviceLocation = coordinates) }
    }

    private suspend fun searchLocations(query: String, locationBias: Coordinates? = null) {
        _state.update { it.copy(isSearching = true, errorMessage = null) }
        try {
            val results = searchLocationsUseCase(query, locationBias)
            _state.update { 
                it.copy(
                    searchResults = results, 
                    isSearching = false, 
                    errorMessage = if (results.isEmpty()) "No se encontraron resultados para '$query'" else null
                ) 
            }
        } catch (t: Throwable) {
            android.util.Log.e("LocationVM", "Search failed: ${t.message}", t)
            _state.update { 
                it.copy(
                    searchResults = emptyList(),
                    isSearching = false, 
                    errorMessage = "No se pudieron obtener resultados para '$query'"
                ) 
            }
        }
    }

    fun onLocationSelected(candidate: LocationCandidate) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val detailedLocation = if (candidate.placeId != null && (candidate.latitude == 0.0 && candidate.longitude == 0.0)) {
                    getPlaceDetailsUseCase(candidate.placeId) ?: candidate
                } else {
                    candidate
                }
                _state.update { 
                    it.copy(
                        selectedLocation = detailedLocation, 
                        isLoading = false,
                        searchQuery = detailedLocation.name,
                        searchResults = emptyList()
                    ) 
                }
            } catch (t: Throwable) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al obtener detalles") }
            }
        }
    }

    fun onMapClick(coordinates: Coordinates) {
        _state.update { 
            it.copy(
                selectedLocation = LocationCandidate(
                    name = "Ubicación seleccionada",
                    address = "${String.format(Locale.US, "%.5f", coordinates.latitude)}, ${String.format(Locale.US, "%.5f", coordinates.longitude)}",
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude
                )
            )
        }
    }

    fun useCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val coords = getCurrentLocationUseCase()
            if (coords != null) {
                _state.update { 
                    it.copy(
                        currentDeviceLocation = coords,
                        selectedLocation = LocationCandidate(
                            name = "Mi ubicación",
                            address = "Cerca de ti",
                            latitude = coords.latitude,
                            longitude = coords.longitude
                        ),
                        isLoading = false 
                    ) 
                }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "No se pudo obtener la ubicación actual") }
            }
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        _state.update { it.copy(isPermissionGranted = isGranted) }
        if (isGranted) {
            useCurrentLocation()
        }
    }
}
