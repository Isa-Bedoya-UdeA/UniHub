package com.unihub.app.features.calendar.presentation.state

import com.unihub.app.features.events.domain.model.Event

data class CalendarUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
