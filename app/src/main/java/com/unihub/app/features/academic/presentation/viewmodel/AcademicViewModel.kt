package com.unihub.app.features.academic.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.academic.StudyOption
import com.unihub.app.features.academic.application.usecase.DeleteAcademicPeriodUseCase
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsByStudyUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.application.usecase.SaveAcademicPeriodUseCase
import com.unihub.app.features.academic.application.usecase.SetCurrentPeriodUseCase
import com.unihub.app.features.academic.application.usecase.SetActiveStudyUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.presentation.state.AcademicState
import com.unihub.app.features.subjects.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.subjects.application.usecase.GetSubjectsUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SubjectWithGrade(
    val subject: Subject,
    val average: Double,
    val progress: Int
)

@HiltViewModel
class AcademicViewModel @Inject constructor(
    private val getStudiesUseCase: GetStudiesUseCase,
    private val getAcademicSummaryUseCase: GetAcademicSummaryUseCase,
    private val getSubjectsUseCase: GetSubjectsUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getAcademicPeriodsByStudyUseCase: GetAcademicPeriodsByStudyUseCase,
    private val setActiveStudyUseCase: SetActiveStudyUseCase,
    private val setCurrentPeriodUseCase: SetCurrentPeriodUseCase,
    private val saveAcademicPeriodUseCase: SaveAcademicPeriodUseCase,
    private val deleteAcademicPeriodUseCase: DeleteAcademicPeriodUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase
) : ViewModel() {

    private val userId = getCurrentUidUseCase() ?: "current_user"

    private val _state = MutableStateFlow(AcademicState())
    val state: StateFlow<AcademicState> = _state.asStateFlow()

    private val _selectedStudyId = MutableStateFlow<String?>(null)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadStudies()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadStudies() {
        viewModelScope.launch {
            try {
                getStudiesUseCase(userId).onEach { studies ->
                    val currentSelection = _selectedStudyId.value
                    val activeStudy = studies.find { it.isActive } ?: studies.firstOrNull()
                    if (currentSelection == null && activeStudy != null) {
                        _selectedStudyId.value = activeStudy.id
                    }
                    val studyOptions = studies.map { StudyOption(id = it.id, name = it.name, institution = it.institution) }
                    _state.update { it.copy(studies = studyOptions, isLoading = false) }
                }.launchIn(viewModelScope)

                _selectedStudyId.onEach { studyId ->
                    if (studyId != null) {
                        _state.update { it.copy(selectedStudyId = studyId) }
                    }
                }.launchIn(viewModelScope)

                _selectedStudyId
                    .flatMapLatest { studyId ->
                        if (studyId == null) return@flatMapLatest flowOf(
                            Triple(emptyList<AcademicPeriod>(), AcademicSummary(0.0, 0.0, 0, 0, 0.0), emptyMap<String, Int>())
                        )
                        combine(
                            getAcademicPeriodsByStudyUseCase(userId, studyId),
                            getAcademicSummaryUseCase(userId, studyId),
                            getAllSubjectsUseCase(userId)
                        ) { periods, summary, allSubjects ->
                            val counts = allSubjects.groupBy { it.academicPeriodId }.mapValues { it.value.size }
                            Triple(periods, summary, counts)
                        }
                    }
                    .onEach { (periods, summary, counts) ->
                        _state.update { it.copy(periods = periods, summary = summary, periodSubjectCounts = counts) }
                    }
                    .launchIn(viewModelScope)

                _selectedStudyId
                    .flatMapLatest { studyId ->
                        if (studyId == null) return@flatMapLatest flowOf(emptyList<SubjectWithGrade>())
                        getAcademicPeriodsByStudyUseCase(userId, studyId)
                            .flatMapLatest { periods ->
                                val today = LocalDate.now()
                                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                
                                val currentPeriod = periods.find { it.isCurrent } ?: periods.find { 
                                    try {
                                        val start = LocalDate.parse(it.startDate, formatter)
                                        val end = LocalDate.parse(it.endDate, formatter)
                                        !today.isBefore(start) && !today.isAfter(end)
                                    } catch (e: Exception) { false }
                                } ?: periods.firstOrNull()
                                
                                if (currentPeriod == null) return@flatMapLatest flowOf(emptyList<SubjectWithGrade>())
                                getSubjectsUseCase(userId, currentPeriod.id)
                                    .flatMapLatest { subjects ->
                                        if (subjects.isEmpty()) return@flatMapLatest flowOf(emptyList<SubjectWithGrade>())
                                        val subjectFlows = subjects.map { subject ->
                                            getGradesBySubjectUseCase(subject.id).combine(flowOf(subject)) { grades, subj ->
                                                val totalWeight = grades.sumOf { it.weight }
                                                val weightedSum = grades.sumOf { it.value * it.weight }
                                                val avg = weightedSum
                                                SubjectWithGrade(subj, avg, (totalWeight * 100).toInt())
                                            }
                                        }
                                        combine(subjectFlows) { it.toList() }
                                    }
                            }
                    }
                    .onEach { subjectsWithGrades ->
                        _state.update { it.copy(subjects = subjectsWithGrades) }
                    }
                    .launchIn(viewModelScope)
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
        viewModelScope.launch {
            try {
                setActiveStudyUseCase(userId, studyId)
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al seleccionar el programa: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun setCurrentPeriod(periodId: String) {
        val period = _state.value.periods.find { it.id == periodId }
        if (period == null) return

        try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val endDate = LocalDate.parse(period.endDate, formatter)
            val today = LocalDate.now()

            if (today.isAfter(endDate)) {
                viewModelScope.launch {
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            message = "No se puede establecer como actual un periodo pasado",
                            type = MessageType.ERROR
                        )
                    )
                }
                return
            }
        } catch (e: Exception) {
            // Fallback if parsing fails
        }

        viewModelScope.launch {
            try {
                setCurrentPeriodUseCase(userId, periodId)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Periodo actualizado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al actualizar el periodo: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun createPeriod(
        studyId: String,
        name: String,
        startDate: String,
        endDate: String,
        isCurrent: Boolean
    ) {
        viewModelScope.launch {
            try {
                val period = AcademicPeriod(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    studyId = studyId,
                    name = name,
                    startDate = startDate,
                    endDate = endDate,
                    isCurrent = isCurrent,
                    createdAt = java.time.Instant.now().toString(),
                    updatedAt = java.time.Instant.now().toString()
                )
                saveAcademicPeriodUseCase(period)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Periodo creado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al crear el periodo: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun addPeriod(period: AcademicPeriod) {
        viewModelScope.launch {
            try {
                saveAcademicPeriodUseCase(period)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Periodo creado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al crear el periodo: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }

    fun deletePeriod(periodId: String) {
        viewModelScope.launch {
            try {
                deleteAcademicPeriodUseCase(periodId)
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Periodo eliminado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al eliminar el periodo: ${e.message ?: "Error desconocido"}",
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
