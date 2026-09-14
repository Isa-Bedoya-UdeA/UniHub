package com.unihub.app.features.academic.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.academic.application.usecase.CalculateRequiredGradeUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.SimulationResult
import com.unihub.app.features.subjects.application.usecase.GetSubjectByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GradeSimulatorState(
    val subjectName: String = "",
    val currentWeightedSum: Double = 0.0,
    val evaluatedWeight: Double = 0.0,
    val targetGrade: Double = 3.0,
    val simulationResult: SimulationResult? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class GradeSimulatorViewModel @Inject constructor(
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    private val calculateRequiredGradeUseCase: CalculateRequiredGradeUseCase
) : ViewModel() {

    var state by mutableStateOf(GradeSimulatorState())
        private set

    fun loadSubject(subjectId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val subject = getSubjectByIdUseCase(subjectId).first()
            val grades = getGradesBySubjectUseCase(subjectId).first()
            
            val totalWeight = grades.sumOf { it.weight }
            val weightedSum = grades.sumOf { it.value * it.weight }
            
            state = state.copy(
                subjectName = subject?.name ?: "",
                currentWeightedSum = weightedSum,
                evaluatedWeight = totalWeight,
                isLoading = false
            )
            calculate()
        }
    }

    fun onTargetGradeChange(value: String) {
        val target = value.toDoubleOrNull() ?: 0.0
        state = state.copy(targetGrade = target)
        calculate()
    }

    private fun calculate() {
        val result = calculateRequiredGradeUseCase(
            currentWeightedSum = state.currentWeightedSum,
            evaluatedWeight = state.evaluatedWeight,
            targetGrade = state.targetGrade
        )
        state = state.copy(simulationResult = result)
    }
}
