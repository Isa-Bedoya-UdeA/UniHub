package com.unihub.app.features.subjects.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.subjects.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.subjects.application.usecase.SaveSubjectUseCase
import com.unihub.app.features.subjects.application.usecase.UpdateSubjectUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SubjectFormState(
    val name: String = "",
    val nameError: String? = null,
    val code: String = "",
    val codeError: String? = null,
    val professor: String = "",
    val professorError: String? = null,
    val credits: String = "",
    val creditsError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class SubjectFormViewModel @Inject constructor(
    private val saveSubjectUseCase: SaveSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase
) : ViewModel() {

    var state by mutableStateOf(SubjectFormState())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentSubjectId: String? = null

    fun onEvent(event: SubjectFormEvent) {
        when (event) {
            is SubjectFormEvent.EnteredName -> state = state.copy(name = event.value, nameError = null)
            is SubjectFormEvent.EnteredCode -> state = state.copy(code = event.value, codeError = null)
            is SubjectFormEvent.EnteredProfessor -> state = state.copy(professor = event.value, professorError = null)
            is SubjectFormEvent.EnteredCredits -> state = state.copy(credits = event.value, creditsError = null)
            is SubjectFormEvent.SaveSubject -> saveSubject()
            is SubjectFormEvent.LoadSubject -> loadSubject(event.id)
        }
    }

    private fun loadSubject(id: String) {
        currentSubjectId = id
        viewModelScope.launch {
            getSubjectByIdUseCase(id).collect { subject ->
                subject?.let {
                    state = state.copy(
                        name = it.name,
                        code = it.code ?: "",
                        professor = it.professor ?: "",
                        credits = it.credits?.toString() ?: ""
                    )
                }
            }
        }
    }

    private fun saveSubject() {
        if (!validateInputs()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            
            val subject = Subject(
                id = currentSubjectId ?: UUID.randomUUID().toString(),
                userId = "user123",
                academicPeriodId = "2026-2",
                name = state.name,
                code = state.code,
                credits = state.credits.toInt(),
                professor = state.professor.ifBlank { null },
                color = "#4F46E5",
                notes = null,
                createdAt = "",
                updatedAt = ""
            )

            if (currentSubjectId == null) {
                saveSubjectUseCase(subject)
            } else {
                updateSubjectUseCase(subject)
            }

            state = state.copy(isLoading = false, isSuccess = true)
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (state.name.trim().length < 3) {
            state = state.copy(nameError = "El nombre debe tener al menos 3 caracteres")
            isValid = false
        }
        
        if (state.code.isBlank()) {
            state = state.copy(codeError = "El código es obligatorio")
            isValid = false
        }
        
        val creditsInt = state.credits.toIntOrNull()
        if (state.credits.isBlank()) {
            state = state.copy(creditsError = "Los créditos son obligatorios")
            isValid = false
        } else if (creditsInt == null || creditsInt <= 0) {
            state = state.copy(creditsError = "Ingresa un número de créditos válido")
            isValid = false
        }

        return isValid
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

sealed class SubjectFormEvent {
    data class EnteredName(val value: String) : SubjectFormEvent()
    data class EnteredCode(val value: String) : SubjectFormEvent()
    data class EnteredProfessor(val value: String) : SubjectFormEvent()
    data class EnteredCredits(val value: String) : SubjectFormEvent()
    data class LoadSubject(val id: String) : SubjectFormEvent()
    object SaveSubject : SubjectFormEvent()
}
