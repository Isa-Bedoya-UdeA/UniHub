package com.unihub.app.features.academic.application.usecase

import javax.inject.Inject

data class SimulationResult(
    val requiredGrade: Double,
    val isPossible: Boolean,
    val isAlreadyPassed: Boolean
)

class CalculateRequiredGradeUseCase @Inject constructor() {
    operator fun invoke(
        currentWeightedSum: Double,
        evaluatedWeight: Double,
        targetGrade: Double = 3.0
    ): SimulationResult {
        val remainingWeight = 1.0 - evaluatedWeight
        
        if (evaluatedWeight >= 1.0) {
            val passed = currentWeightedSum >= targetGrade
            return SimulationResult(0.0, passed, passed)
        }

        val needed = (targetGrade - currentWeightedSum) / remainingWeight
        
        return SimulationResult(
            requiredGrade = needed,
            isPossible = needed <= 5.0,
            isAlreadyPassed = currentWeightedSum >= targetGrade
        )
    }
}
