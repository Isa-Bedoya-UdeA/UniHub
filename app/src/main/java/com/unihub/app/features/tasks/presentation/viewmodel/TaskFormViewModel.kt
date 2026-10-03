package com.unihub.app.features.tasks.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.academic.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.tasks.application.usecase.GetTaskByIdUseCase
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskUseCase
import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskReminderType
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.model.TaskTag
import com.unihub.app.features.tasks.domain.repository.TagRepository
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.presentation.state.TaskFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val getAllSubjectsUseCase: com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase,
    private val tagRepository: TagRepository,
    private val taskRepository: TaskRepository,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId = getCurrentUidUseCase() ?: "current_user"

    private val _state = MutableStateFlow(TaskFormState())
    val state: StateFlow<TaskFormState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentTaskId: String? = null
    private var subjectId: String? = null

    init {
        currentTaskId = savedStateHandle.get<String>("taskId")
        val initialSubjectId = savedStateHandle.get<String>("subjectId")
        if (!initialSubjectId.isNullOrBlank() && initialSubjectId != "{subjectId}") {
            subjectId = initialSubjectId
            _state.update { it.copy(selectedSubjectId = initialSubjectId) }
        }
        loadSubjects()
        syncAndLoadTags()
        currentTaskId?.let { loadTask(it) }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            try {
                getAllSubjectsUseCase(userId).collect { subjects ->
                    _state.update { it.copy(availableSubjects = subjects) }
                }
            } catch (e: Exception) {
                Log.e("TaskFormViewModel", "Error loading subjects: ${e.message}")
            }
        }
    }

    private fun syncAndLoadTags() {
        viewModelScope.launch {
            try {
                // Primero sincronizar desde Firestore
                tagRepository.syncTags(userId)
                // Luego cargar las tags locales
                tagRepository.getTagsByUser(userId).collect { tags ->
                    _state.update { it.copy(availableTags = tags) }
                }
            } catch (e: Exception) {
                Log.e("TaskFormViewModel", "Error loading tags: ${e.message}")
            }
        }
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
            is TaskFormEvent.StatusChanged -> {
                _state.update { it.copy(status = event.value) }
            }
            is TaskFormEvent.SubjectChanged -> {
                _state.update { it.copy(selectedSubjectId = event.subjectId) }
            }
            is TaskFormEvent.ReminderChanged -> {
                _state.update { it.copy(reminderType = event.type, reminderValue = event.value) }
            }
            is TaskFormEvent.ClearReminder -> {
                _state.update { it.copy(reminderType = null, reminderValue = null) }
            }
            is TaskFormEvent.DeadlineReminderToggled -> {
                _state.update { it.copy(isDeadlineReminderEnabled = event.isEnabled) }
            }
            is TaskFormEvent.TagInputChanged -> {
                _state.update { it.copy(tagInput = event.value) }
            }
            is TaskFormEvent.AddTag -> {
                addTag(event.name)
            }
            is TaskFormEvent.RemoveTag -> {
                _state.update { state ->
                    state.copy(selectedTags = state.selectedTags.filter { it.id != event.tagId })
                }
            }
            is TaskFormEvent.SaveTask -> {
                saveTask()
            }
            is TaskFormEvent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun addTag(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            try {
                val existingTag = _state.value.availableTags.find { it.name.equals(trimmed, ignoreCase = true) }
                val tag = existingTag ?: Tag(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = trimmed,
                    createdAt = Instant.now().toString()
                )
                val currentTags = _state.value.selectedTags
                if (currentTags.any { it.id == tag.id }) {
                    _uiEvent.emit(UiEvent.ShowMessage("Error 409: La etiqueta '${trimmed}' ya está seleccionada", MessageType.ERROR))
                    _state.update { it.copy(tagInput = "") }
                    return@launch
                }
                _state.update { 
                    it.copy(
                        selectedTags = currentTags + tag, 
                        tagInput = "",
                        availableTags = if (existingTag == null) it.availableTags + tag else it.availableTags
                    ) 
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Error al agregar tag: ${e.message}", MessageType.ERROR))
            }
        }
    }

    fun selectExistingTag(tag: Tag) {
        val currentTags = _state.value.selectedTags
        if (currentTags.any { it.id == tag.id }) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowMessage("Error 409: La etiqueta '${tag.name}' ya está seleccionada", MessageType.ERROR))
            }
            return
        }
        _state.update { it.copy(selectedTags = currentTags + tag, tagInput = "") }
    }

    fun getFilteredTags(query: String): List<Tag> {
        if (query.isBlank()) return emptyList()
        val selectedIds = _state.value.selectedTags.map { it.id }
        return _state.value.availableTags.filter { 
            it.name.contains(query, ignoreCase = true) && it.id !in selectedIds
        }
    }

    private     fun loadTask(id: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                // Get task data
                val task = getTaskByIdUseCase(id).first()
                task?.let {
                    _state.update { state ->
                        state.copy(
                            title = it.title,
                            description = it.description ?: "",
                            dueDate = it.dueAt ?: "",
                            priority = it.priority,
                            status = it.status,
                            reminderType = it.reminderType,
                            reminderValue = it.reminderValue,
                            isDeadlineReminderEnabled = it.isDeadlineReminderEnabled,
                            selectedSubjectId = it.subjectId,
                            isLoading = false
                        )
                    }
                    subjectId = it.subjectId
                    
                    // Load task tags immediately
                    val taskTags = tagRepository.getTagsByTask(id).first()
                    val tags = taskTags.mapNotNull { taskTag ->
                        tagRepository.getTagById(taskTag.tagId).first()
                    }
                    _state.update { it.copy(selectedTags = tags) }
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
        if (!validateInputs()) {
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                // Save all selected tags to database (Room + Firestore)
                // This ensures tags exist before the task references them
                for (tag in _state.value.selectedTags) {
                    tagRepository.saveTag(tag)
                }

                val now = Instant.now().toString()
                var academicPeriodId: String? = null
                val finalSubjectId = _state.value.selectedSubjectId ?: subjectId
                finalSubjectId?.let { sid ->
                    try {
                        val subject = getSubjectByIdUseCase(sid).first()
                        academicPeriodId = subject?.academicPeriodId
                    } catch (_: Exception) { }
                }

                val task = Task(
                    id = currentTaskId ?: UUID.randomUUID().toString(),
                    userId = userId,
                    academicPeriodId = academicPeriodId,
                    subjectId = finalSubjectId,
                    title = _state.value.title,
                    description = _state.value.description.ifBlank { null },
                    dueAt = _state.value.dueDate.ifBlank { null },
                    priority = _state.value.priority,
                    status = _state.value.status,
                    reminderType = _state.value.reminderType,
                    reminderValue = _state.value.reminderValue,
                    isDeadlineReminderEnabled = _state.value.isDeadlineReminderEnabled,
                    createdAt = if (currentTaskId == null) now else (loadOriginalCreatedAt() ?: now),
                    updatedAt = now
                )

                val tagIds = _state.value.selectedTags.map { it.id }

                if (currentTaskId == null) {
                    saveTaskUseCase(task)
                } else {
                    // Remove old tags
                    val oldTaskTags = tagRepository.getTagsByTask(task.id).first()
                    oldTaskTags.forEach { taskTag ->
                        tagRepository.removeTaskTag(task.id, taskTag.tagId)
                    }
                    updateTaskUseCase(task)
                }

                // Add tags to task
                _state.value.selectedTags.forEach { tag ->
                    tagRepository.addTaskTag(TaskTag(taskId = task.id, tagId = tag.id))
                }

                // Save task tags to Firestore
                taskRepository.saveTaskTags(userId, task.id, tagIds)

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

    private suspend fun loadOriginalCreatedAt(): String? {
        return try {
            getTaskByIdUseCase(currentTaskId!!).first()?.createdAt
        } catch (_: Exception) {
            null
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (_state.value.title.isBlank()) {
            _state.update { it.copy(titleError = "El titulo es obligatorio") }
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
    data class StatusChanged(val value: TaskStatus) : TaskFormEvent()
    data class SubjectChanged(val subjectId: String?) : TaskFormEvent()
    data class ReminderChanged(val type: TaskReminderType?, val value: Int?) : TaskFormEvent()
    object ClearReminder : TaskFormEvent()
    data class DeadlineReminderToggled(val isEnabled: Boolean) : TaskFormEvent()
    data class TagInputChanged(val value: String) : TaskFormEvent()
    data class AddTag(val name: String) : TaskFormEvent()
    data class RemoveTag(val tagId: String) : TaskFormEvent()
    object SaveTask : TaskFormEvent()
    object ClearError : TaskFormEvent()
}
