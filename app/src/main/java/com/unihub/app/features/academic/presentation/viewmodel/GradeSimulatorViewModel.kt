package com.unihub.app.features.academic.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.subjects.application.usecase.GetSubjectByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GradeSimulatorState(
    val subjectName: String = "",
    val currentAverage: Double = 0.0,
    val evaluatedWeight: Double = 0.0,
    val targetGrade: Double = 3.0,
    val requiredGrade: Double? = null,
    val isPossible: Boolean = true,
    val isAlreadyPassed: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class GradeSimulatorViewModel @Inject constructor(
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase
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
            val currentAvg = if (totalWeight > 0.0) weightedSum / totalWeight else 0.0
            
            state = state.copy(
                subjectName = subject?.name ?: "",
                currentAverage = currentAvg,
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
        val target = state.targetGrade
        val weightedSum = state.currentAverage * state.evaluatedWeight
        val remainingWeight = 1.0 - state.evaluatedWeight
        
        if (state.evaluatedWeight >= 1.0) {
            state = state.copy(
                requiredGrade = null,
                isAlreadyPassed = weightedSum >= target,
                isPossible = weightedSum >= target
            )
            return
        }

        val needed = (target - weightedSum) / remainingWeight
        
        state = state.copy(
            requiredGrade = needed,
            isPossible = needed <= 5.0,
            isAlreadyPassed = weightedSum >= target
        )
    }
}
