package com.unihub.app.features.events.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.events.application.usecase.GetEventByIdUseCase
import com.unihub.app.features.events.application.usecase.SaveEventUseCase
import com.unihub.app.features.events.application.usecase.SaveRecurrenceDayUseCase
import com.unihub.app.features.events.application.usecase.SaveRecurrenceRuleUseCase
import com.unihub.app.features.events.application.usecase.UpdateEventUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import com.unihub.app.features.events.presentation.state.EventFormState
import com.unihub.app.features.location.application.usecase.GetLocationByIdUseCase
import com.unihub.app.features.location.application.usecase.SaveLocationUseCase
import com.unihub.app.features.location.application.usecase.OpenExternalMapUseCase
import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.domain.repository.LocationCandidate
import com.unihub.app.features.subjects.application.usecase.GetAllSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val deleteEventUseCase: com.unihub.app.features.events.application.usecase.DeleteEventUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val openExternalMapUseCase: OpenExternalMapUseCase,
    private val saveRecurrenceRuleUseCase: SaveRecurrenceRuleUseCase,
    private val saveRecurrenceDayUseCase: SaveRecurrenceDayUseCase,
    private val deleteRecurrenceRuleUseCase: com.unihub.app.features.events.application.usecase.DeleteRecurrenceRuleUseCase,
    private val getAcademicPeriodsUseCase: com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase,
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
        viewModelScope.launch {
            try {
                getAcademicPeriodsUseCase("current_user").collect { periods ->
                    _state.update { it.copy(academicPeriods = periods) }
                }
            } catch (e: Exception) {
                // Ignore gracefully
            }
        }
    }

    fun onEvent(event: EventFormEvent) {
        when (event) {
            is EventFormEvent.EnteredTitle -> _state.update { it.copy(title = event.value, titleError = null) }
            is EventFormEvent.EnteredDate -> _state.update { it.copy(date = event.value, dateError = null) }
            is EventFormEvent.EnteredStartTime -> _state.update { it.copy(startTime = event.value, startTimeError = null) }
            is EventFormEvent.EnteredEndTime -> _state.update { it.copy(endTime = event.value, endTimeError = null) }
            is EventFormEvent.LocationTypeChanged -> {
                _state.update { 
                    it.copy(
                        locationType = event.value,
                        meetingUrl = if (event.value != LocationType.REMOTE) "" else it.meetingUrl,
                        meetingUrlError = null,
                        selectedLocation = if (event.value != LocationType.PHYSICAL) null else it.selectedLocation
                    )
                }
            }
            is EventFormEvent.EventTypeChanged -> _state.update { it.copy(eventType = event.value) }
            is EventFormEvent.SubjectSelected -> _state.update { it.copy(subjectId = event.subjectId) }
            is EventFormEvent.EnteredMeetingUrl -> _state.update { it.copy(meetingUrl = event.value, meetingUrlError = null) }
            is EventFormEvent.EnteredNotes -> _state.update { it.copy(notes = event.value) }
            is EventFormEvent.ReminderAdded -> addReminder(event.reminder)
            is EventFormEvent.ReminderRemoved -> removeReminder(event.reminderId)
            is EventFormEvent.ReminderToggled -> toggleReminder(event.reminderId, event.isEnabled)
            is EventFormEvent.RemindersCleared -> _state.update { it.copy(reminders = emptyList()) }
            is EventFormEvent.LocationSelected -> _state.update { it.copy(selectedLocation = event.value) }
            is EventFormEvent.OpenInMaps -> openInMaps(event.context)
            is EventFormEvent.RecurrenceToggled -> _state.update { it.copy(isRecurring = event.enabled) }
            is EventFormEvent.RecurrenceDayToggled -> toggleRecurrenceDay(event.dayOfWeek)
            is EventFormEvent.RecurrenceStartDateChanged -> _state.update { it.copy(recurrenceStartDate = event.value) }
            is EventFormEvent.RecurrenceEndDateChanged -> _state.update { it.copy(recurrenceEndDate = event.value) }
            is EventFormEvent.SaveEvent -> saveEvent()
            is EventFormEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
            is EventFormEvent.AcademicPeriodSelected -> {
                val period = _state.value.academicPeriods.find { it.id == event.periodId }
                if (period != null) {
                    _state.update { it.copy(recurrenceStartDate = period.startDate, recurrenceEndDate = period.endDate) }
                }
            }
            is EventFormEvent.DeleteEvent -> deleteEvent()
        }
    }

    private fun deleteEvent() {
        val id = currentEventId ?: return
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                deleteEventUseCase(id)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _uiEvent.emit(UiEvent.ShowMessage("Evento eliminado exitosamente", MessageType.SUCCESS))
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(UiEvent.ShowMessage("Error al eliminar el evento: ${e.message}", MessageType.ERROR))
            }
        }
    }

    private fun toggleRecurrenceDay(dayOfWeek: Int) {
        _state.update { state ->
            val newDays = if (dayOfWeek in state.recurrenceDays) {
                state.recurrenceDays - dayOfWeek
            } else {
                state.recurrenceDays + dayOfWeek
            }
            state.copy(recurrenceDays = newDays)
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

    private fun openInMaps(context: android.content.Context) {
        val location = _state.value.selectedLocation ?: return
        openExternalMapUseCase(context, location.latitude, location.longitude, location.name)
    }

    private fun loadEvent(id: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                getEventByIdUseCase(id).collect { event ->
                    event?.let { e ->
                        val internalDate = e.startAt.split("T").firstOrNull() ?: ""
                        val parts = internalDate.split("-")
                        val spanishDate = if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else internalDate
                        
                        _state.update { state ->
                            state.copy(
                                title = e.title,
                                date = spanishDate,
                                startTime = e.startAt.split("T").lastOrNull() ?: "",
                                endTime = e.endAt.split("T").lastOrNull() ?: "",
                                locationType = e.locationType,
                                eventType = e.eventType,
                                subjectId = e.subjectId,
                                meetingUrl = e.meetingUrl ?: "",
                                notes = e.notes ?: "",
                                reminders = e.reminders,
                                isRecurring = e.recurrenceRuleId != null,
                                recurrenceDays = e.recurrenceDays.toSet(),
                                recurrenceStartDate = e.recurrenceRule?.startDate?.let {
                                    val parts = it.split("-")
                                    if (parts.size == 3 && parts[0].length == 4) "${parts[2]}-${parts[1]}-${parts[0]}" else it
                                } ?: "",
                                recurrenceEndDate = e.recurrenceRule?.endDate?.let {
                                    val parts = it.split("-")
                                    if (parts.size == 3 && parts[0].length == 4) "${parts[2]}-${parts[1]}-${parts[0]}" else it
                                } ?: "",
                                isLoading = e.locationId != null
                            )
                        }

                        e.locationId?.let { locId ->
                            viewModelScope.launch {
                                getLocationByIdUseCase.invoke(locId).collect { location ->
                                    location?.let { loc ->
                                        _state.update { s ->
                                            s.copy(
                                                selectedLocation = LocationCandidate(
                                                    name = loc.name ?: "",
                                                    address = loc.address ?: "",
                                                    latitude = loc.latitude ?: 0.0,
                                                    longitude = loc.longitude ?: 0.0,
                                                    placeId = loc.placeId
                                                ),
                                                isLoading = false
                                            )
                                        }
                                    } ?: _state.update { it.copy(isLoading = false) }
                                }
                            }
                        } ?: _state.update { it.copy(isLoading = false) }
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
                
                val effectiveDate = if (_state.value.date.isBlank() && _state.value.isRecurring) {
                    _state.value.recurrenceStartDate
                } else {
                    _state.value.date
                }
                
                val dateParts = effectiveDate.split("-")
                val isoDate = if (dateParts.size == 3) "${dateParts[2]}-${dateParts[1]}-${dateParts[0]}" else effectiveDate

                val startAt = if (_state.value.startTime.isNotBlank()) "${isoDate}T${_state.value.startTime}" else "${isoDate}T00:00"
                val endAt = if (_state.value.endTime.isNotBlank()) "${isoDate}T${_state.value.endTime}" else "${isoDate}T23:59"

                val now = Instant.now().toString()
                val eventId = currentEventId ?: UUID.randomUUID().toString()
                
                // If editing, handle cleanup of old recurrence
                if (currentEventId != null) {
                    getEventByIdUseCase(currentEventId!!).first()?.recurrenceRuleId?.let { oldRuleId ->
                        deleteRecurrenceRuleUseCase(oldRuleId)
                    }
                }

                var locationId: String? = null
                if (_state.value.locationType == LocationType.PHYSICAL && _state.value.selectedLocation != null) {
                    val candidate = _state.value.selectedLocation!!
                    val locId = UUID.randomUUID().toString()
                    val location = Location(
                        id = locId,
                        userId = "current_user",
                        name = candidate.name,
                        address = candidate.address,
                        latitude = candidate.latitude,
                        longitude = candidate.longitude,
                        placeId = candidate.placeId,
                        createdAt = now,
                        updatedAt = now
                    )
                    saveLocationUseCase(location)
                    locationId = locId
                }

                val remindersWithEventId = _state.value.reminders.map { reminder ->
                    reminder.copy(eventId = eventId)
                }
                
                var recurrenceRuleId: String? = null
                if (_state.value.isRecurring && _state.value.recurrenceDays.isNotEmpty()) {
                    val ruleId = UUID.randomUUID().toString()
                    val startDate = if (_state.value.recurrenceStartDate.isNotBlank()) {
                        val parts = _state.value.recurrenceStartDate.split("-")
                        if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else isoDate
                    } else isoDate
                    val endDate = if (_state.value.recurrenceEndDate.isNotBlank()) {
                        val parts = _state.value.recurrenceEndDate.split("-")
                        if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else isoDate
                    } else isoDate
                    
                    val recurrenceRule = RecurrenceRule(
                        id = ruleId,
                        userId = "current_user",
                        frequency = "WEEKLY",
                        interval = 1,
                        startDate = startDate,
                        endDate = endDate,
                        createdAt = now,
                        updatedAt = now
                    )
                    saveRecurrenceRuleUseCase(recurrenceRule)
                    
                    _state.value.recurrenceDays.forEach { dayOfWeek ->
                        val recurrenceDay = RecurrenceDay(
                            recurrenceRuleId = ruleId,
                            dayOfWeek = dayOfWeek
                        )
                        saveRecurrenceDayUseCase(recurrenceDay)
                    }
                    recurrenceRuleId = ruleId
                }
                
                val event = Event(
                    id = eventId,
                    userId = "current_user",
                    academicPeriodId = null,
                    subjectId = _state.value.subjectId,
                    locationId = locationId,
                    recurrenceRuleId = recurrenceRuleId,
                    title = _state.value.title,
                    startAt = startAt,
                    endAt = endAt,
                    locationType = _state.value.locationType,
                    eventType = _state.value.eventType,
                    meetingUrl = if (_state.value.locationType == LocationType.REMOTE) _state.value.meetingUrl.trim().takeIf { it.isNotBlank() } else null,
                    notes = _state.value.notes.ifBlank { null },
                    reminders = remindersWithEventId,
                    createdAt = now,
                    updatedAt = now
                )
                
                android.util.Log.d("EventFormVM", "Saving event: ${event.title}, locationType: ${event.locationType}, meetingUrl: ${event.meetingUrl}")

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
        val isDateValid = Pattern.matches(dateRegex, _state.value.date)
        
        if (!isDateValid) {
            val isRecurringValid = _state.value.isRecurring && 
                                 Pattern.matches(dateRegex, _state.value.recurrenceStartDate)
            
            if (!isRecurringValid) {
                _state.update { it.copy(dateError = "Formato de fecha inválido (DD-MM-AAAA)") }
                isValid = false
            }
        }
        
        if (_state.value.locationType == LocationType.REMOTE) {
            val trimmedUrl = _state.value.meetingUrl.trim()
            if (trimmedUrl.isBlank()) {
                _state.update { it.copy(meetingUrlError = "El enlace de la reunión es obligatorio") }
                isValid = false
            } else if (!isValidUrl(trimmedUrl)) {
                _state.update { it.copy(meetingUrlError = "Ingresa una URL válida (https://...)") }
                isValid = false
            }
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

    private fun isValidUrl(url: String): Boolean {
        return try {
            val uri = android.net.Uri.parse(url)
            uri.scheme in listOf("http", "https") && (uri.host?.isNotBlank() ?: false)
        } catch (e: Exception) {
            false
        }
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
    data class LocationSelected(val value: LocationCandidate?) : EventFormEvent()
    data class OpenInMaps(val context: android.content.Context) : EventFormEvent()
    data class RecurrenceToggled(val enabled: Boolean) : EventFormEvent()
    data class RecurrenceDayToggled(val dayOfWeek: Int) : EventFormEvent()
    data class RecurrenceStartDateChanged(val value: String) : EventFormEvent()
    data class RecurrenceEndDateChanged(val value: String) : EventFormEvent()
    data class AcademicPeriodSelected(val periodId: String) : EventFormEvent()
    object RemindersCleared : EventFormEvent()
    object SaveEvent : EventFormEvent()
    object DeleteEvent : EventFormEvent()
    object ClearError : EventFormEvent()
}
