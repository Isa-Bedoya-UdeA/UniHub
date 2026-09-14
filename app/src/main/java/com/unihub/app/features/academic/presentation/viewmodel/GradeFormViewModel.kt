package com.unihub.app.features.academic.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.SaveGradeUseCase
import com.unihub.app.features.academic.application.usecase.UpdateGradeUseCase
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.presentation.state.GradeFormState
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
class GradeFormViewModel @Inject constructor(
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(GradeFormState())
    val state: StateFlow<GradeFormState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var currentGradeId: String? = null
    private var subjectId: String = ""

    init {
        subjectId = savedStateHandle.get<String>("subjectId") ?: ""
        currentGradeId = savedStateHandle.get<String>("gradeId")
        currentGradeId?.let { loadGrade(subjectId, it) }
    }

    fun onEvent(event: GradeFormEvent) {
        when (event) {
            is GradeFormEvent.EnteredName -> _state.update { it.copy(name = event.value, nameError = null) }
            is GradeFormEvent.EnteredValue -> _state.update { it.copy(value = event.value, valueError = null) }
            is GradeFormEvent.EnteredWeight -> _state.update { it.copy(weight = event.value, weightError = null) }
            is GradeFormEvent.SaveGrade -> saveGrade()
            is GradeFormEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadGrade(subjectId: String, gradeId: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val grades = getGradesBySubjectUseCase(subjectId).first()
                val grade = grades.find { it.id == gradeId }
                grade?.let {
                    _state.update { state ->
                        state.copy(
                            name = it.name,
                            value = it.value.toString(),
                            weight = (it.weight * 100).toInt().toString(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar la nota: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun saveGrade() {
        if (!validateInputs()) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                val now = Instant.now().toString()
                val grade = Grade(
                    id = currentGradeId ?: UUID.randomUUID().toString(),
                    userId = "current_user",
                    subjectId = subjectId,
                    academicPeriodId = "",
                    name = _state.value.name,
                    value = _state.value.value.toDoubleOrNull() ?: 0.0,
                    weight = (_state.value.weight.toDoubleOrNull() ?: 0.0) / 100.0,
                    notes = null,
                    createdAt = now,
                    updatedAt = now
                )

                if (currentGradeId == null) saveGradeUseCase(grade) else updateGradeUseCase(grade)

                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = if (currentGradeId == null) "Nota creada exitosamente" else "Nota actualizada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al guardar la nota: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (_state.value.name.isBlank()) {
            _state.update { it.copy(nameError = "El nombre es obligatorio") }
            isValid = false
        }
        val gradeValue = _state.value.value.toDoubleOrNull()
        if (gradeValue == null || gradeValue < 0.0 || gradeValue > 5.0) {
            _state.update { it.copy(valueError = "La nota debe estar entre 0.0 y 5.0") }
            isValid = false
        }
        val weightValue = _state.value.weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0.0 || weightValue > 100.0) {
            _state.update { it.copy(weightError = "El peso debe estar entre 1 y 100") }
            isValid = false
        }
        return isValid
    }
}

sealed class GradeFormEvent {
    data class EnteredName(val value: String) : GradeFormEvent()
    data class EnteredValue(val value: String) : GradeFormEvent()
    data class EnteredWeight(val value: String) : GradeFormEvent()
    object SaveGrade : GradeFormEvent()
    object ClearError : GradeFormEvent()
}
