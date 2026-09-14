package com.unihub.app.features.events.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.events.application.usecase.GetEventByIdUseCase
import com.unihub.app.features.events.application.usecase.SaveEventUseCase
import com.unihub.app.features.events.application.usecase.UpdateEventUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.presentation.state.EventFormState
import com.unihub.app.features.subjects.application.usecase.GetAllSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class EventFormViewModel @Inject constructor(
    private val saveEventUseCase: SaveEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EventFormState())
    val state: StateFlow<EventFormState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentEventId: String? = null

    init {
        currentEventId = savedStateHandle.get<String>("eventId")
        loadSubjects()
        currentEventId?.let { loadEvent(it) }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            try {
                getAllSubjectsUseCase("current_user").collect { subjects ->
                    _state.update { it.copy(subjects = subjects) }
                }
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar las materias: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun onEvent(event: EventFormEvent) {
        when (event) {
            is EventFormEvent.EnteredTitle -> _state.update { it.copy(title = event.value, titleError = null) }
            is EventFormEvent.EnteredDate -> _state.update { it.copy(date = event.value, dateError = null) }
            is EventFormEvent.EnteredStartTime -> _state.update { it.copy(startTime = event.value, startTimeError = null) }
            is EventFormEvent.EnteredEndTime -> _state.update { it.copy(endTime = event.value, endTimeError = null) }
            is EventFormEvent.LocationTypeChanged -> _state.update { it.copy(locationType = event.value) }
            is EventFormEvent.EventTypeChanged -> _state.update { it.copy(eventType = event.value) }
            is EventFormEvent.SubjectSelected -> _state.update { it.copy(subjectId = event.subjectId) }
            is EventFormEvent.EnteredMeetingUrl -> _state.update { it.copy(meetingUrl = event.value, meetingUrlError = null) }
            is EventFormEvent.EnteredNotes -> _state.update { it.copy(notes = event.value) }
            is EventFormEvent.ReminderAdded -> addReminder(event.reminder)
            is EventFormEvent.ReminderRemoved -> removeReminder(event.reminderId)
            is EventFormEvent.ReminderToggled -> toggleReminder(event.reminderId, event.isEnabled)
            is EventFormEvent.RemindersCleared -> _state.update { it.copy(reminders = emptyList()) }
            is EventFormEvent.SaveEvent -> saveEvent()
            is EventFormEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun addReminder(reminder: EventReminder) {
        _state.update { it.copy(reminders = it.reminders + reminder) }
    }

    private fun removeReminder(reminderId: String) {
        _state.update { it.copy(reminders = it.reminders.filter { r -> r.id != reminderId }) }
    }

    private fun toggleReminder(reminderId: String, isEnabled: Boolean) {
        _state.update { 
            it.copy(
                reminders = it.reminders.map { r ->
                    if (r.id == reminderId) r.copy(isEnabled = isEnabled) else r
                }
            )
        }
    }

    private fun loadEvent(id: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                getEventByIdUseCase(id).collect { event ->
                    event?.let {
                        val internalDate = it.startAt.split("T").firstOrNull() ?: ""
                        val parts = internalDate.split("-")
                        val spanishDate = if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else internalDate
                        
                        _state.update { state ->
                            state.copy(
                                title = it.title,
                                date = spanishDate,
                                startTime = it.startAt.split("T").lastOrNull() ?: "",
                                endTime = it.endAt.split("T").lastOrNull() ?: "",
                                locationType = it.locationType,
                                eventType = it.eventType,
                                subjectId = it.subjectId,
                                meetingUrl = it.meetingUrl ?: "",
                                notes = it.notes ?: "",
                                reminders = it.reminders,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar el evento: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun saveEvent() {
        if (!validateInputs()) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val dateParts = _state.value.date.split("-")
                val isoDate = if (dateParts.size == 3) "${dateParts[2]}-${dateParts[1]}-${dateParts[0]}" else _state.value.date

                val startAt = if (_state.value.startTime.isNotBlank()) "${isoDate}T${_state.value.startTime}" else "${isoDate}T00:00"
                val endAt = if (_state.value.endTime.isNotBlank()) "${isoDate}T${_state.value.endTime}" else "${isoDate}T23:59"

                val now = Instant.now().toString()
                val eventId = currentEventId ?: UUID.randomUUID().toString()
                
                val remindersWithEventId = _state.value.reminders.map { reminder ->
                    reminder.copy(eventId = eventId)
                }
                
                val event = Event(
                    id = eventId,
                    userId = "current_user",
                    academicPeriodId = null,
                    subjectId = _state.value.subjectId,
                    locationId = null,
                    recurrenceRuleId = null,
                    title = _state.value.title,
                    startAt = startAt,
                    endAt = endAt,
                    locationType = _state.value.locationType,
                    eventType = _state.value.eventType,
                    meetingUrl = if (_state.value.locationType == LocationType.REMOTE) _state.value.meetingUrl else null,
                    notes = _state.value.notes.ifBlank { null },
                    reminders = remindersWithEventId,
                    createdAt = now,
                    updatedAt = now
                )

                if (currentEventId == null) {
                    saveEventUseCase(event)
                } else {
                    updateEventUseCase(event)
                }

                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = if (currentEventId == null) "Evento creado exitosamente" else "Evento actualizado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al guardar el evento: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (_state.value.title.trim().isEmpty()) {
            _state.update { it.copy(titleError = "El título es obligatorio") }
            isValid = false
        }
        
        val dateRegex = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(19|20)\\d\\d$"
        if (!Pattern.matches(dateRegex, _state.value.date)) {
            _state.update { it.copy(dateError = "Formato de fecha inválido (DD-MM-AAAA)") }
            isValid = false
        }
        
        if (_state.value.locationType == LocationType.REMOTE && _state.value.meetingUrl.isBlank()) {
            _state.update { it.copy(meetingUrlError = "El enlace de la reunión es obligatorio") }
            isValid = false
        }

        if (_state.value.startTime.isNotBlank() && _state.value.endTime.isNotBlank()) {
            try {
                val start = _state.value.startTime.replace(":", "").toInt()
                val end = _state.value.endTime.replace(":", "").toInt()
                if (end <= start) {
                    _state.update { it.copy(endTimeError = "La hora de fin debe ser posterior a la hora de inicio") }
                    isValid = false
                }
            } catch (e: Exception) {
                _state.update { it.copy(startTimeError = "Formato de hora inválido") }
                isValid = false
            }
        }

        return isValid
    }
}

sealed class EventFormEvent {
    data class EnteredTitle(val value: String) : EventFormEvent()
    data class EnteredDate(val value: String) : EventFormEvent()
    data class EnteredStartTime(val value: String) : EventFormEvent()
    data class EnteredEndTime(val value: String) : EventFormEvent()
    data class LocationTypeChanged(val value: LocationType) : EventFormEvent()
    data class EventTypeChanged(val value: EventType) : EventFormEvent()
    data class SubjectSelected(val subjectId: String?) : EventFormEvent()
    data class EnteredMeetingUrl(val value: String) : EventFormEvent()
    data class EnteredNotes(val value: String) : EventFormEvent()
    data class ReminderAdded(val reminder: EventReminder) : EventFormEvent()
    data class ReminderRemoved(val reminderId: String) : EventFormEvent()
    data class ReminderToggled(val reminderId: String, val isEnabled: Boolean) : EventFormEvent()
    object RemindersCleared : EventFormEvent()
    object SaveEvent : EventFormEvent()
    object ClearError : EventFormEvent()
}
