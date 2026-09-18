package com.unihub.app.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.academic.application.usecase.DeleteStudyUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.application.usecase.SaveStudyUseCase
import com.unihub.app.features.academic.application.usecase.SetActiveStudyUseCase
import com.unihub.app.features.academic.domain.model.Study
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StudySettingsViewModel @Inject constructor(
    private val getStudiesUseCase: GetStudiesUseCase,
    private val saveStudyUseCase: SaveStudyUseCase,
    private val deleteStudyUseCase: DeleteStudyUseCase,
    private val setActiveStudyUseCase: SetActiveStudyUseCase
) : ViewModel() {

    val state: StateFlow<List<Study>> = getStudiesUseCase("current_user")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun addStudy(name: String, institution: String, credits: Int, approvedCredits: Int? = null, cumulativeGpa: Double? = null) {
        if (name.isBlank() || institution.isBlank() || credits <= 0) return
        viewModelScope.launch {
            try {
                val now = Instant.now().toString()
                val study = Study(
                    id = UUID.randomUUID().toString(),
                    userId = "current_user",
                    name = name.trim(),
                    institution = institution.trim(),
                    totalCredits = credits,
                    approvedCredits = approvedCredits,
                    cumulativeGpa = cumulativeGpa,
                    isActive = false,
                    createdAt = now,
                    updatedAt = now
                )
                saveStudyUseCase(study)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Programa académico creado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al crear el programa: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun updateStudy(study: Study, name: String, institution: String, credits: Int, approvedCredits: Int? = null, cumulativeGpa: Double? = null) {
        if (name.isBlank() || institution.isBlank() || credits <= 0) return
        viewModelScope.launch {
            try {
                val updatedStudy = study.copy(
                    name = name.trim(),
                    institution = institution.trim(),
                    totalCredits = credits,
                    approvedCredits = approvedCredits,
                    cumulativeGpa = cumulativeGpa,
                    updatedAt = Instant.now().toString()
                )
                saveStudyUseCase(updatedStudy)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Programa académico actualizado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al actualizar el programa: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun deleteStudy(id: String) {
        viewModelScope.launch {
            try {
                deleteStudyUseCase(id)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Programa académico eliminado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al eliminar el programa: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun setActive(id: String) {
        viewModelScope.launch {
            try {
                setActiveStudyUseCase("current_user", id)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Programa activo actualizado",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al actualizar el programa activo: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }
}
