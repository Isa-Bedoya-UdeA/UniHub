package com.unihub.app.features.events.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.events.application.usecase.DeleteEventUseCase
import com.unihub.app.features.events.application.usecase.GetEventByIdUseCase
import com.unihub.app.features.events.presentation.state.EventDetailsState
import com.unihub.app.features.location.application.usecase.GetLocationByIdUseCase
import com.unihub.app.features.location.application.usecase.OpenExternalMapUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val openExternalMapUseCase: OpenExternalMapUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    private val _state = MutableStateFlow(EventDetailsState())
    val state: StateFlow<EventDetailsState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadEventDetails()
    }

    private fun loadEventDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getEventByIdUseCase(eventId)
                .onEach { event ->
                    if (event != null) {
                        android.util.Log.d("EventDetailsVM", "Loaded event: ${event.title}, locationType: ${event.locationType}, meetingUrl: ${event.meetingUrl}")
                        _state.update { it.copy(event = event) }
                        if (event.locationId != null) {
                            loadLocation(event.locationId)
                        } else {
                            _state.update { it.copy(isLoading = false) }
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Evento no encontrado") }
                    }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect()
        }
    }

    private fun loadLocation(locationId: String) {
        viewModelScope.launch {
            getLocationByIdUseCase.invoke(locationId)
                .onEach { location ->
                    _state.update { it.copy(location = location, isLoading = false) }
                }
                .catch { 
                    _state.update { it.copy(isLoading = false) }
                }
                .collect()
        }
    }

    fun deleteEvent(onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteEventUseCase(eventId)
            onDeleted()
        }
    }

    fun openInMaps(context: android.content.Context) {
        val location = _state.value.location ?: return
        if (location.latitude != null && location.longitude != null) {
            openExternalMapUseCase(context, location.latitude, location.longitude, location.name)
        }
    }

    fun openMeetingUrl(context: android.content.Context) {
        val event = _state.value.event
        if (event == null) {
            android.util.Log.e("EventDetailsVM", "Event is null")
            return
        }
        
        val meetingUrl = event.meetingUrl
        android.util.Log.d("EventDetailsVM", "Meeting URL: $meetingUrl, LocationType: ${event.locationType}")
        
        if (meetingUrl.isNullOrBlank()) {
            android.util.Log.e("EventDetailsVM", "Meeting URL is null or blank")
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowMessage("Este evento no tiene enlace de reunión", MessageType.ERROR))
            }
            return
        }
        
        try {
            val uri = android.net.Uri.parse(meetingUrl)
            android.util.Log.d("EventDetailsVM", "Parsed URI: $uri")
            
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                android.util.Log.e("EventDetailsVM", "No app found to handle URL")
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowMessage("No se encontró una aplicación para abrir el enlace", MessageType.ERROR))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("EventDetailsVM", "Error opening URL", e)
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowMessage("Error al abrir el enlace: ${e.message}", MessageType.ERROR))
            }
        }
    }
}
