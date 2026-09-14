package com.unihub.app.features.events.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.events.application.usecase.DeleteEventUseCase
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.application.usecase.SaveEventUseCase
import com.unihub.app.features.events.application.usecase.UpdateEventUseCase
import com.unihub.app.features.events.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    getEventsUseCase: GetEventsUseCase,
    private val saveEventUseCase: SaveEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val userId = "current_user"
    // Fetch a wider range for the calendar to be useful
    private val today = LocalDate.now(ZoneId.systemDefault())
    private val rangeStart = today.minusMonths(6).toString()
    private val rangeEnd = today.plusMonths(12).toString()

    val events: StateFlow<List<Event>> = getEventsUseCase(userId, rangeStart, rangeEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEvent(event: Event) {
        viewModelScope.launch {
            saveEventUseCase(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            updateEventUseCase(event)
        }
    }

    fun removeEvent(id: String) {
        viewModelScope.launch {
            deleteEventUseCase(id)
        }
    }
}
