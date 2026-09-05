package com.unihub.app.features.academic.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.SaveGradeUseCase
import com.unihub.app.features.academic.application.usecase.UpdateGradeUseCase
import com.unihub.app.features.academic.domain.model.Grade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class GradeFormState(
    val name: String = "",
    val nameError: String? = null,
    val value: String = "",
    val valueError: String? = null,
    val weight: String = "",
    val weightError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class GradeFormViewModel @Inject constructor(
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase
) : ViewModel() {

    var state by mutableStateOf(GradeFormState())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentGradeId: String? = null
    private var subjectId: String = ""

    fun onEvent(event: GradeFormEvent) {
        when (event) {
            is GradeFormEvent.Init -> {
                this.subjectId = event.subjectId
                if (event.gradeId != null) {
                    loadGrade(event.subjectId, event.gradeId)
                }
            }
            is GradeFormEvent.EnteredName -> state = state.copy(name = event.value, nameError = null)
            is GradeFormEvent.EnteredValue -> state = state.copy(value = event.value, valueError = null)
            is GradeFormEvent.EnteredWeight -> state = state.copy(weight = event.value, weightError = null)
            is GradeFormEvent.SaveGrade -> saveGrade()
        }
    }

    private fun loadGrade(subjectId: String, gradeId: String) {
        currentGradeId = gradeId
        viewModelScope.launch {
            val grades = getGradesBySubjectUseCase(subjectId).first()
            val grade = grades.find { it.id == gradeId }
            grade?.let {
                state = state.copy(
                    name = it.name,
                    value = it.value.toString(),
                    weight = (it.weight * 100).toInt().toString()
                )
            }
        }
    }

    private fun saveGrade() {
        if (!validateInputs()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val grade = Grade(
                id = currentGradeId ?: UUID.randomUUID().toString(),
                userId = "user123",
                subjectId = subjectId,
                academicPeriodId = "2026-2",
                name = state.name,
                value = state.value.toDoubleOrNull() ?: 0.0,
                weight = (state.weight.toDoubleOrNull() ?: 0.0) / 100.0,
                notes = null,
                createdAt = "",
                updatedAt = ""
            )

            if (currentGradeId == null) saveGradeUseCase(grade) else updateGradeUseCase(grade)

            state = state.copy(isLoading = false, isSuccess = true)
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        if (state.name.isBlank()) {
            state = state.copy(nameError = "El nombre es obligatorio")
            isValid = false
        }
        val gradeValue = state.value.toDoubleOrNull()
        if (gradeValue == null || gradeValue < 0.0 || gradeValue > 5.0) {
            state = state.copy(valueError = "La nota debe estar entre 0.0 y 5.0")
            isValid = false
        }
        val weightValue = state.weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0.0 || weightValue > 100.0) {
            state = state.copy(weightError = "El peso debe estar entre 1 y 100")
            isValid = false
        }
        return isValid
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
    }
}

sealed class GradeFormEvent {
    data class Init(val subjectId: String, val gradeId: String? = null) : GradeFormEvent()
    data class EnteredName(val value: String) : GradeFormEvent()
    data class EnteredValue(val value: String) : GradeFormEvent()
    data class EnteredWeight(val value: String) : GradeFormEvent()
    object SaveGrade : GradeFormEvent()
}
