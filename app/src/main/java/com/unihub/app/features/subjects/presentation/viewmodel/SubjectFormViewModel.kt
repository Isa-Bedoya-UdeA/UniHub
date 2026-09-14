package com.unihub.app.features.subjects.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.subjects.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.subjects.application.usecase.SaveSubjectUseCase
import com.unihub.app.features.subjects.application.usecase.UpdateSubjectUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.presentation.state.SubjectFormState
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
class SubjectFormViewModel @Inject constructor(
    private val saveSubjectUseCase: SaveSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val getAcademicPeriodsUseCase: GetAcademicPeriodsUseCase,
    private val getStudiesUseCase: GetStudiesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectFormState())
    val state: StateFlow<SubjectFormState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentSubjectId: String? = null

    init {
        currentSubjectId = savedStateHandle.get<String>("subjectId")
        loadBaseData()
        currentSubjectId?.let { loadSubject(it) }
    }

    private fun loadBaseData() {
        viewModelScope.launch {
            try {
                getStudiesUseCase("current_user").collect { studies ->
                    _state.update { state ->
                        state.copy(
                            studies = studies,
                            studyId = if (state.studyId == null && studies.isNotEmpty()) {
                                studies.find { it.isActive }?.id ?: studies.first().id
                            } else state.studyId
                        )
                    }
                    _state.value.studyId?.let { loadPeriodsForStudy(it) }
                }
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar los datos: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun loadPeriodsForStudy(studyId: String) {
        viewModelScope.launch {
            try {
                getAcademicPeriodsUseCase("current_user").collect { periods ->
                    val filtered = periods.filter { it.studyId == studyId }
                    _state.update { state ->
                        state.copy(
                            periods = filtered,
                            academicPeriodId = if (state.academicPeriodId == null && filtered.isNotEmpty()) {
                                filtered.find { it.isCurrent }?.id ?: filtered.first().id
                            } else state.academicPeriodId
                        )
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar los periodos: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun onEvent(event: SubjectFormEvent) {
        when (event) {
            is SubjectFormEvent.EnteredName -> _state.update { it.copy(name = event.value, nameError = null) }
            is SubjectFormEvent.EnteredCode -> _state.update { it.copy(code = event.value, codeError = null) }
            is SubjectFormEvent.EnteredProfessor -> _state.update { it.copy(professor = event.value, professorError = null) }
            is SubjectFormEvent.EnteredCredits -> _state.update { it.copy(credits = event.value, creditsError = null) }
            is SubjectFormEvent.StudySelected -> {
                _state.update { it.copy(studyId = event.value, studyError = null, academicPeriodId = null) }
                loadPeriodsForStudy(event.value)
            }
            is SubjectFormEvent.PeriodSelected -> _state.update { it.copy(academicPeriodId = event.value, academicPeriodError = null) }
            is SubjectFormEvent.SaveSubject -> saveSubject()
            is SubjectFormEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    fun loadSubject(id: String) {
        currentSubjectId = id
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                getSubjectByIdUseCase(id).collect { subject ->
                    subject?.let {
                        _state.update { state ->
                            state.copy(
                                name = it.name,
                                code = it.code ?: "",
                                professor = it.professor ?: "",
                                credits = it.credits?.toString() ?: "",
                                studyId = it.studyId,
                                academicPeriodId = it.academicPeriodId,
                                isLoading = false
                            )
                        }
                        loadPeriodsForStudy(it.studyId)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar la materia: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun saveSubject() {
        if (!validateInputs()) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                
                val now = Instant.now().toString()
                val subject = Subject(
                    id = currentSubjectId ?: UUID.randomUUID().toString(),
                    userId = "current_user",
                    studyId = _state.value.studyId!!,
                    academicPeriodId = _state.value.academicPeriodId!!,
                    name = _state.value.name,
                    code = _state.value.code,
                    credits = _state.value.credits.toInt(),
                    professor = _state.value.professor.ifBlank { null },
                    color = "#4F46E5",
                    notes = null,
                    createdAt = now,
                    updatedAt = now
                )

                if (currentSubjectId == null) {
                    saveSubjectUseCase(subject)
                } else {
                    updateSubjectUseCase(subject)
                }

                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = if (currentSubjectId == null) "Materia creada exitosamente" else "Materia actualizada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al guardar la materia: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (_state.value.name.trim().length < 3) {
            _state.update { it.copy(nameError = "El nombre debe tener al menos 3 caracteres") }
            isValid = false
        }
        
        if (_state.value.code.isBlank()) {
            _state.update { it.copy(codeError = "El código es obligatorio") }
            isValid = false
        }
        
        val creditsInt = _state.value.credits.toIntOrNull()
        if (_state.value.credits.isBlank()) {
            _state.update { it.copy(creditsError = "Los créditos son obligatorios") }
            isValid = false
        } else if (creditsInt == null || creditsInt <= 0) {
            _state.update { it.copy(creditsError = "Ingresa un número de créditos válido") }
            isValid = false
        }

        if (_state.value.studyId == null) {
            _state.update { it.copy(studyError = "Debes seleccionar un programa académico") }
            isValid = false
        }

        if (_state.value.academicPeriodId == null) {
            _state.update { it.copy(academicPeriodError = "Debes seleccionar un periodo académico") }
            isValid = false
        }

        return isValid
    }
}

sealed class SubjectFormEvent {
    data class EnteredName(val value: String) : SubjectFormEvent()
    data class EnteredCode(val value: String) : SubjectFormEvent()
    data class EnteredProfessor(val value: String) : SubjectFormEvent()
    data class EnteredCredits(val value: String) : SubjectFormEvent()
    data class StudySelected(val value: String) : SubjectFormEvent()
    data class PeriodSelected(val value: String) : SubjectFormEvent()
    object SaveSubject : SubjectFormEvent()
    object ClearError : SubjectFormEvent()
}
