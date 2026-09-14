package com.unihub.app.features.tasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.tasks.application.usecase.DeleteTaskUseCase
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TasksUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val userId = "current_user"

    val tasks: StateFlow<List<Task>> = getTasksUseCase(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                saveTaskUseCase(task)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Tarea creada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al crear la tarea: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun removeTask(id: String) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(id)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Tarea eliminada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al eliminar la tarea: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }
}
