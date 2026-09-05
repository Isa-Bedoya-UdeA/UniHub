package com.unihub.app.features.academic.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.SaveAcademicPeriodUseCase
import com.unihub.app.features.academic.application.usecase.DeleteAcademicPeriodUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.subjects.application.usecase.GetSubjectsUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubjectWithGrade(
    val subject: Subject,
    val average: Double,
    val progress: Int
)

data class AcademicUiState(
    val summary: AcademicSummary = AcademicSummary(0.0, 0.0, 0, 0.0),
    val subjects: List<SubjectWithGrade> = emptyList(),
    val periods: List<AcademicPeriod> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AcademicViewModel @Inject constructor(
    getAcademicSummaryUseCase: GetAcademicSummaryUseCase,
    getSubjectsUseCase: GetSubjectsUseCase,
    getAcademicPeriodsUseCase: GetAcademicPeriodsUseCase,
    private val saveAcademicPeriodUseCase: SaveAcademicPeriodUseCase,
    private val deleteAcademicPeriodUseCase: DeleteAcademicPeriodUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    private val repository: AcademicRepository // Directly for simplicity in mock
) : ViewModel() {

    private val userId = "user123"
    private val periodId = "2026-2"

    private val _state = MutableStateFlow(AcademicUiState())
    val state = _state.asStateFlow()

    init {
        combine(
            getAcademicSummaryUseCase(userId),
            getSubjectsUseCase(userId, periodId),
            getAcademicPeriodsUseCase(userId)
        ) { summary, subjects, periods ->
            val subjectsWithGrades = subjects.map { subject ->
                val grades = getGradesBySubjectUseCase(subject.id).first()
                val weightedSum = grades.sumOf { it.value * it.weight }
                val totalWeight = grades.sumOf { it.weight }
                val avg = if (totalWeight > 0) weightedSum / totalWeight else 0.0
                val progress = (totalWeight * 100).toInt().coerceIn(0, 100)
                
                SubjectWithGrade(subject, avg, progress)
            }
            
            AcademicUiState(
                summary = summary,
                subjects = subjectsWithGrades,
                periods = periods,
                isLoading = false
            )
        }.onEach { newState ->
            _state.value = newState
        }.launchIn(viewModelScope)
    }

    fun addPeriod(period: AcademicPeriod) {
        viewModelScope.launch {
            saveAcademicPeriodUseCase(period)
        }
    }

    fun deletePeriod(id: String) {
        viewModelScope.launch {
            deleteAcademicPeriodUseCase(id)
        }
    }

    fun setCurrentPeriod(id: String) {
        viewModelScope.launch {
            repository.setCurrentPeriod(id)
        }
    }
}
