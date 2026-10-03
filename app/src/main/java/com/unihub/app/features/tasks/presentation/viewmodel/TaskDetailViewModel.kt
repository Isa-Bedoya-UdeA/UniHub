package com.unihub.app.features.tasks.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.tasks.application.usecase.DeleteTaskUseCase
import com.unihub.app.features.tasks.application.usecase.GetTaskByIdUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.model.TaskTag
import com.unihub.app.features.tasks.domain.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailState(
    val task: Task? = null,
    val tags: List<Tag> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentTaskId: String? = null

    fun loadTask(id: String) {
        currentTaskId = id
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                // Get task data
                val task = getTaskByIdUseCase(id).first()
                _state.update { it.copy(task = task, isLoading = false) }
                
                // Load task tags immediately
                val taskTags = tagRepository.getTagsByTask(id).first()
                val tagObjects = taskTags.mapNotNull { taskTag ->
                    tagRepository.getTagById(taskTag.tagId).first()
                }
                _state.update { it.copy(tags = tagObjects) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun toggleStatus() {
        val task = _state.value.task ?: return
        viewModelScope.launch {
            try {
                val newStatus = when (task.status) {
                    TaskStatus.PENDING -> TaskStatus.IN_PROGRESS
                    TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                    TaskStatus.COMPLETED -> TaskStatus.PENDING
                }
                updateTaskStatusUseCase(task.id, newStatus)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Error: ${e.message}", MessageType.ERROR))
            }
        }
    }

    fun deleteTask() {
        val id = currentTaskId ?: return
        viewModelScope.launch {
            try {
                deleteTaskUseCase(id)
                _uiEvent.emit(UiEvent.ShowMessage("Tarea eliminada", MessageType.SUCCESS))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Error: ${e.message}", MessageType.ERROR))
            }
        }
    }
}
