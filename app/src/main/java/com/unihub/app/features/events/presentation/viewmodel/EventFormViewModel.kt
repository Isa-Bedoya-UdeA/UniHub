package com.unihub.app.features.events.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.events.application.usecase.GetEventByIdUseCase
import com.unihub.app.features.events.application.usecase.SaveEventUseCase
import com.unihub.app.features.events.application.usecase.UpdateEventUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.LocationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern
import javax.inject.Inject

data class EventFormState(
    val title: String = "",
    val titleError: String? = null,
    val date: String = "",
    val dateError: String? = null,
    val startTime: String = "",
    val startTimeError: String? = null,
    val endTime: String = "",
    val endTimeError: String? = null,
    val locationType: LocationType = LocationType.NONE,
    val meetingUrl: String = "",
    val meetingUrlError: String? = null,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class EventFormViewModel @Inject constructor(
    private val saveEventUseCase: SaveEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase
) : ViewModel() {

    var state by mutableStateOf(EventFormState())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentEventId: String? = null

    fun onEvent(event: EventFormEvent) {
        when (event) {
            is EventFormEvent.Init -> {
                if (event.eventId != null) {
                    loadEvent(event.eventId)
                }
            }
            is EventFormEvent.EnteredTitle -> state = state.copy(title = event.value, titleError = null)
            is EventFormEvent.EnteredDate -> state = state.copy(date = event.value, dateError = null)
            is EventFormEvent.EnteredStartTime -> state = state.copy(startTime = event.value, startTimeError = null)
            is EventFormEvent.EnteredEndTime -> state = state.copy(endTime = event.value, endTimeError = null)
            is EventFormEvent.LocationTypeChanged -> state = state.copy(locationType = event.value)
            is EventFormEvent.EnteredMeetingUrl -> state = state.copy(meetingUrl = event.value, meetingUrlError = null)
            is EventFormEvent.EnteredNotes -> state = state.copy(notes = event.value)
            is EventFormEvent.SaveEvent -> saveEvent()
            is EventFormEvent.LoadEvent -> loadEvent(event.id)
        }
    }

    private fun loadEvent(id: String) {
        currentEventId = id
        viewModelScope.launch {
            getEventByIdUseCase(id).collect { event ->
                event?.let {
                    val internalDate = it.startAt.split("T").firstOrNull() ?: ""
                    val parts = internalDate.split("-")
                    val spanishDate = if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else internalDate
                    
                    state = state.copy(
                        title = it.title,
                        date = spanishDate,
                        startTime = it.startAt.split("T").lastOrNull() ?: "",
                        endTime = it.endAt.split("T").lastOrNull() ?: "",
                        locationType = it.locationType,
                        meetingUrl = it.meetingUrl ?: "",
                        notes = it.notes ?: ""
                    )
                }
            }
        }
    }

    private fun saveEvent() {
        if (!validateInputs()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            
            val dateParts = state.date.split("-")
            val isoDate = if (dateParts.size == 3) "${dateParts[2]}-${dateParts[1]}-${dateParts[0]}" else state.date

            val startAt = if (state.startTime.isNotBlank()) "${isoDate}T${state.startTime}" else "${isoDate}T00:00"
            val endAt = if (state.endTime.isNotBlank()) "${isoDate}T${state.endTime}" else "${isoDate}T23:59"

            val event = Event(
                id = currentEventId ?: UUID.randomUUID().toString(),
                userId = "user123",
                academicPeriodId = null,
                subjectId = null,
                locationId = null,
                recurrenceRuleId = null,
                title = state.title,
                startAt = startAt,
                endAt = endAt,
                locationType = state.locationType,
                meetingUrl = if (state.locationType == LocationType.REMOTE) state.meetingUrl else null,
                notes = state.notes.ifBlank { null },
                createdAt = "",
                updatedAt = ""
            )

            if (currentEventId == null) {
                saveEventUseCase(event)
            } else {
                updateEventUseCase(event)
            }

            state = state.copy(isLoading = false, isSuccess = true)
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (state.title.trim().isEmpty()) {
            state = state.copy(titleError = "El título es obligatorio")
            isValid = false
        }
        
        val dateRegex = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(19|20)\\d\\d$"
        if (!Pattern.matches(dateRegex, state.date)) {
            state = state.copy(dateError = "Formato DD-MM-AAAA")
            isValid = false
        }
        
        if (state.locationType == LocationType.REMOTE && state.meetingUrl.isBlank()) {
            state = state.copy(meetingUrlError = "El enlace es obligatorio")
            isValid = false
        }

        if (state.startTime.isNotBlank() && state.endTime.isNotBlank()) {
            try {
                val start = state.startTime.replace(":", "").toInt()
                val end = state.endTime.replace(":", "").toInt()
                if (end <= start) {
                    state = state.copy(endTimeError = "Debe ser posterior al inicio")
                    isValid = false
                }
            } catch (e: Exception) {
                isValid = false
            }
        }

        return isValid
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
    }
}

sealed class EventFormEvent {
    data class Init(val eventId: String?) : EventFormEvent()
    data class EnteredTitle(val value: String) : EventFormEvent()
    data class EnteredDate(val value: String) : EventFormEvent()
    data class EnteredStartTime(val value: String) : EventFormEvent()
    data class EnteredEndTime(val value: String) : EventFormEvent()
    data class LocationTypeChanged(val value: LocationType) : EventFormEvent()
    data class EnteredMeetingUrl(val value: String) : EventFormEvent()
    data class EnteredNotes(val value: String) : EventFormEvent()
    data class LoadEvent(val id: String) : EventFormEvent()
    object SaveEvent : EventFormEvent()
}
