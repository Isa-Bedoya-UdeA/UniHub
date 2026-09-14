package com.unihub.app.features.dashboard.presentation.state

import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.tasks.domain.model.Task

data class DashboardState(
    val summary: AcademicSummary = AcademicSummary(0.0, 0.0, 0, 0, 0.0),
    val upcomingEvents: List<Event> = emptyList(),
    val pendingTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
