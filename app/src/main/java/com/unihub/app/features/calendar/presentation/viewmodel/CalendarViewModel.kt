package com.unihub.app.features.calendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.calendar.application.usecase.GetCalendarEventsUseCase
import com.unihub.app.features.calendar.presentation.state.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getCalendarEventsUseCase: GetCalendarEventsUseCase
) : ViewModel() {

    private val userId = "current_user"
    private val systemZone = ZoneId.systemDefault()

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getCalendarEventsUseCase(
                userId,
                LocalDate.now(systemZone).minusMonths(6).toString(),
                LocalDate.now(systemZone).plusMonths(12).toString()
            ).catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar eventos"
                )
            }.collect { events ->
                _state.value = _state.value.copy(
                    events = events,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
}
