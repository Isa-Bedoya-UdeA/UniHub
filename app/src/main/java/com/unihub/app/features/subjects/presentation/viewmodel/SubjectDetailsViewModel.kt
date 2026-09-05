package com.unihub.app.features.subjects.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.academic.application.usecase.DeleteGradeUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.subjects.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.tasks.application.usecase.DeleteTaskUseCase
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubjectDetailsState(
    val subject: Subject? = null,
    val grades: List<Grade> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SubjectDetailsViewModel @Inject constructor(
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    private val deleteGradeUseCase: DeleteGradeUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectDetailsState())
    val state = _state.asStateFlow()

    fun loadSubject(id: String) {
        _state.update { it.copy(isLoading = true) }
        
        combine(
            getSubjectByIdUseCase(id),
            getGradesBySubjectUseCase(id),
            getTasksUseCase("user123")
        ) { subject, grades, tasks ->
            SubjectDetailsState(
                subject = subject,
                grades = grades,
                tasks = tasks.filter { it.subjectId == id },
                isLoading = false
            )
        }.onEach { newState ->
            _state.value = newState
        }.launchIn(viewModelScope)
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.PENDING else TaskStatus.COMPLETED
            updateTaskStatusUseCase(task.id, newStatus)
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
        }
    }

    fun deleteGrade(id: String) {
        viewModelScope.launch {
            deleteGradeUseCase(id)
        }
    }
}
