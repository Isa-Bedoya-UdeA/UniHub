package com.unihub.app.features.subjects.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.academic.StudyOption
import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.subjects.application.usecase.DeleteSubjectUseCase
import com.unihub.app.features.subjects.application.usecase.GetSubjectsUseCase
import com.unihub.app.features.subjects.presentation.state.SubjectsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val getSubjectsUseCase: GetSubjectsUseCase,
    private val getAcademicPeriodsUseCase: GetAcademicPeriodsUseCase,
    private val getStudiesUseCase: GetStudiesUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    private val _selectedStudyId = MutableStateFlow<String?>(null)
    private val _selectedPeriodId = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow(SubjectsState())
    val state: StateFlow<SubjectsState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadData() {
        viewModelScope.launch {
            try {
                getStudiesUseCase("current_user").onEach { studies ->
                    if (_selectedStudyId.value == null) {
                        val active = studies.find { it.isActive } ?: studies.firstOrNull()
                        _selectedStudyId.value = active?.id
                    }
                    val studyOptions = studies.map { StudyOption(id = it.id, name = it.name, institution = it.institution) }
                    _state.update { it.copy(studies = studyOptions, selectedStudyId = _selectedStudyId.value) }
                }.launchIn(viewModelScope)

                _selectedStudyId.flatMapLatest { studyId ->
                    if (studyId == null) return@flatMapLatest emptyFlow<List<AcademicPeriod>>()
                    getAcademicPeriodsUseCase("current_user").onEach { periods ->
                        val filteredPeriods = periods.filter { it.studyId == studyId }
                        if (_selectedPeriodId.value == null && filteredPeriods.isNotEmpty()) {
                            _selectedPeriodId.value = filteredPeriods.find { it.isCurrent }?.id ?: filteredPeriods.first().id
                        }
                        _state.update { it.copy(periods = filteredPeriods, selectedPeriodId = _selectedPeriodId.value) }
                    }
                }.launchIn(viewModelScope)

                combine(_selectedStudyId, _selectedPeriodId) { studyId, periodId ->
                    Pair(studyId, periodId)
                }.flatMapLatest { (studyId, periodId) ->
                    if (studyId != null && periodId != null) {
                        getSubjectsUseCase("current_user", periodId)
                    } else {
                        emptyFlow()
                    }
                }.onEach { subjects ->
                    _state.update { it.copy(subjects = subjects, isLoading = false) }
                }.launchIn(viewModelScope)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al cargar los datos: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun selectStudy(studyId: String) {
        _selectedStudyId.value = studyId
        _selectedPeriodId.value = null
        _state.update { it.copy(selectedStudyId = studyId, selectedPeriodId = null) }
    }

    fun selectPeriod(periodId: String) {
        _selectedPeriodId.value = periodId
        _state.update { it.copy(selectedPeriodId = periodId) }
    }

    fun deleteSubject(id: String) {
        viewModelScope.launch {
            try {
                deleteSubjectUseCase(id)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Materia eliminada exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al eliminar la materia: ${e.message ?: "Error desconocido"}",
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
