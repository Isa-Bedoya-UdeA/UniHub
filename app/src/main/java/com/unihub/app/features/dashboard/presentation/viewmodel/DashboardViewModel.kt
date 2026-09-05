package com.unihub.app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getAcademicSummaryUseCase: GetAcademicSummaryUseCase,
    getTasksUseCase: GetTasksUseCase,
    getEventsUseCase: GetEventsUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    private val userId = "user123"
    private val bogotaZone = ZoneId.of("America/Bogota")

    val academicSummary: StateFlow<AcademicSummary> = getAcademicSummaryUseCase(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AcademicSummary(0.0, 0.0, 0, 0.0))

    val pendingTasks: StateFlow<List<Task>> = getTasksUseCase(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingEvents: StateFlow<List<Event>> = getEventsUseCase(
        userId, 
        LocalDate.now(bogotaZone).toString(), 
        LocalDate.now(bogotaZone).plusDays(2).toString()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.PENDING else TaskStatus.COMPLETED
            updateTaskStatusUseCase(task.id, newStatus)
        }
    }
}
