package com.unihub.app.features.tasks.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.tasks.application.usecase.GetTaskByIdUseCase
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TaskFormState(
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val dueDate: String = "",
    val dueDateError: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase
) : ViewModel() {

    var state by mutableStateOf(TaskFormState())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentTaskId: String? = null
    private var subjectId: String? = null

    fun onEvent(event: TaskFormEvent) {
        when (event) {
            is TaskFormEvent.Init -> {
                this.subjectId = event.subjectId
                if (event.taskId != null) {
                    loadTask(event.taskId)
                }
            }
            is TaskFormEvent.EnteredTitle -> {
                state = state.copy(title = event.value, titleError = null)
            }
            is TaskFormEvent.EnteredDescription -> {
                state = state.copy(description = event.value)
            }
            is TaskFormEvent.EnteredDueDate -> {
                state = state.copy(dueDate = event.value, dueDateError = null)
            }
            is TaskFormEvent.PriorityChanged -> {
                state = state.copy(priority = event.value)
            }
            is TaskFormEvent.SaveTask -> {
                saveTask()
            }
            is TaskFormEvent.LoadTask -> {
                loadTask(event.id)
            }
        }
    }

    private fun loadTask(id: String) {
        currentTaskId = id
        viewModelScope.launch {
            getTaskByIdUseCase(id).collect { task ->
                task?.let {
                    state = state.copy(
                        title = it.title,
                        description = it.description ?: "",
                        dueDate = it.dueAt ?: "",
                        priority = it.priority
                    )
                }
            }
        }
    }

    private fun saveTask() {
        if (!validateInputs()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            
            val task = Task(
                id = currentTaskId ?: UUID.randomUUID().toString(),
                userId = "user123",
                academicPeriodId = "2026-2",
                subjectId = subjectId,
                title = state.title,
                description = state.description.ifBlank { null },
                dueAt = state.dueDate.ifBlank { null },
                priority = state.priority,
                status = TaskStatus.PENDING,
                notes = null,
                createdAt = "",
                updatedAt = ""
            )

            if (currentTaskId == null) {
                saveTaskUseCase(task)
            } else {
                updateTaskUseCase(task)
            }

            state = state.copy(isLoading = false, isSuccess = true)
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (state.title.isBlank()) {
            state = state.copy(titleError = "El título es obligatorio")
            isValid = false
        }
        return isValid
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
    }
}

sealed class TaskFormEvent {
    data class Init(val subjectId: String?, val taskId: String? = null) : TaskFormEvent()
    data class EnteredTitle(val value: String) : TaskFormEvent()
    data class EnteredDescription(val value: String) : TaskFormEvent()
    data class EnteredDueDate(val value: String) : TaskFormEvent()
    data class PriorityChanged(val value: TaskPriority) : TaskFormEvent()
    data class LoadTask(val id: String) : TaskFormEvent()
    object SaveTask : TaskFormEvent()
}
