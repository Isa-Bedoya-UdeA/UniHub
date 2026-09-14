package com.unihub.app.features.tasks.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.tasks.application.usecase.GetTaskByIdUseCase
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.presentation.state.TaskFormState
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
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TaskFormState())
    val state: StateFlow<TaskFormState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentTaskId: String? = null
    private var subjectId: String? = null

    init {
        currentTaskId = savedStateHandle.get<String>("taskId")
        subjectId = savedStateHandle.get<String>("subjectId")
        currentTaskId?.let { loadTask(it) }
    }

    fun onEvent(event: TaskFormEvent) {
        when (event) {
            is TaskFormEvent.EnteredTitle -> {
                _state.update { it.copy(title = event.value, titleError = null) }
            }
            is TaskFormEvent.EnteredDescription -> {
                _state.update { it.copy(description = event.value) }
            }
            is TaskFormEvent.EnteredDueDate -> {
                _state.update { it.copy(dueDate = event.value, dueDateError = null) }
            }
            is TaskFormEvent.PriorityChanged -> {
                _state.update { it.copy(priority = event.value) }
            }
            is TaskFormEvent.ReminderAtChanged -> {
                _state.update { it.copy(reminderAt = event.value) }
            }
            is TaskFormEvent.DeadlineReminderToggled -> {
                _state.update { it.copy(isDeadlineReminderEnabled = event.isEnabled) }
            }
            is TaskFormEvent.SaveTask -> {
                saveTask()
            }
            is TaskFormEvent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun loadTask(id: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                getTaskByIdUseCase(id).collect { task ->
                    task?.let {
                        _state.update { state ->
                            state.copy(
                                title = it.title,
                                description = it.description ?: "",
                                dueDate = it.dueAt ?: "",
                                priority = it.priority,
                                reminderAt = it.reminderAt,
                                isDeadlineReminderEnabled = it.isDeadlineReminderEnabled,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar la tarea: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun saveTask() {
        if (!validateInputs()) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val now = Instant.now().toString()
                val task = Task(
                    id = currentTaskId ?: UUID.randomUUID().toString(),
                    userId = "current_user",
                    academicPeriodId = "2026-2",
                    subjectId = subjectId,
                    title = _state.value.title,
                    description = _state.value.description.ifBlank { null },
                    dueAt = _state.value.dueDate.ifBlank { null },
                    priority = _state.value.priority,
                    status = TaskStatus.PENDING,
                    notes = null,
                    reminderAt = _state.value.reminderAt,
                    isDeadlineReminderEnabled = _state.value.isDeadlineReminderEnabled,
                    createdAt = now,
                    updatedAt = now
                )

                if (currentTaskId == null) {
                    saveTaskUseCase(task)
                } else {
                    updateTaskUseCase(task)
                }

                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = if (currentTaskId == null) "Tarea creada exitosamente" else "Tarea actualizada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al guardar la tarea: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (_state.value.title.isBlank()) {
            _state.update { it.copy(titleError = "El título es obligatorio") }
            isValid = false
        }
        return isValid
    }
}

sealed class TaskFormEvent {
    data class EnteredTitle(val value: String) : TaskFormEvent()
    data class EnteredDescription(val value: String) : TaskFormEvent()
    data class EnteredDueDate(val value: String) : TaskFormEvent()
    data class PriorityChanged(val value: TaskPriority) : TaskFormEvent()
    data class ReminderAtChanged(val value: String?) : TaskFormEvent()
    data class DeadlineReminderToggled(val isEnabled: Boolean) : TaskFormEvent()
    object SaveTask : TaskFormEvent()
    object ClearError : TaskFormEvent()
}
