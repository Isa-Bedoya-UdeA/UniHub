package com.unihub.app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.dashboard.application.usecase.GetDashboardDataUseCase
import com.unihub.app.features.dashboard.presentation.state.DashboardState
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardDataUseCase: GetDashboardDataUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase
) : ViewModel() {

    private val userId = "current_user"

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadDashboardData(getDashboardDataUseCase)
    }

    private fun loadDashboardData(getDashboardDataUseCase: GetDashboardDataUseCase) {
        viewModelScope.launch {
            getDashboardDataUseCase(userId)
                .map { data ->
                    DashboardState(
                        summary = data.summary,
                        upcomingEvents = data.upcomingEvents,
                        pendingTasks = data.pendingTasks,
                        isLoading = false
                    )
                }
                .catch { e ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            message = "Error al cargar el dashboard: ${e.message ?: "Error desconocido"}",
                            type = MessageType.ERROR
                        )
                    )
                }
                .collect { newState ->
                    _state.value = newState
                }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.PENDING else TaskStatus.COMPLETED
                updateTaskStatusUseCase(task.id, newStatus)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = if (newStatus == TaskStatus.COMPLETED) "Tarea marcada como completada" else "Tarea marcada como pendiente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al actualizar la tarea: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
