package com.unihub.app.features.subjects.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.academic.application.usecase.DeleteGradeUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.subjects.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.subjects.presentation.state.SubjectDetailsState
import com.unihub.app.features.tasks.application.usecase.DeleteTaskUseCase
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SubjectDetailsViewModel @Inject constructor(
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    private val deleteGradeUseCase: DeleteGradeUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectDetailsState())
    val state: StateFlow<SubjectDetailsState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadSubject(id: String) {
        _state.update { it.copy(isLoading = true) }
        
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val end = "2030-01-01T00:00:00"

        viewModelScope.launch {
            try {
                combine(
                    getSubjectByIdUseCase(id),
                    getGradesBySubjectUseCase(id),
                    getTasksUseCase("current_user"),
                    getEventsUseCase("current_user", now, end)
                ) { subject, grades, tasks, events ->
                    val subjectTasks = tasks.filter { it.subjectId == id }
                    val subjectEvents = events.filter { it.subjectId == id }
                    val nextClass = subjectEvents
                        .filter { it.eventType == EventType.CLASS }
                        .minByOrNull { it.startAt }

                    SubjectDetailsState(
                        subject = subject,
                        grades = grades,
                        tasks = subjectTasks,
                        events = subjectEvents,
                        nextClass = nextClass,
                        isLoading = false
                    )
                }.onEach { newState ->
                    _state.value = newState
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar los datos de la materia: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun toggleTask(task: Task) {
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

    fun deleteTask(id: String) {
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

    fun deleteGrade(id: String) {
        viewModelScope.launch {
            try {
                deleteGradeUseCase(id)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Nota eliminada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al eliminar la nota: ${e.message ?: "Error desconocido"}",
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
