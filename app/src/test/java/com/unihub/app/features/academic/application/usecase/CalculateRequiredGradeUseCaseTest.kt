package com.unihub.app.features.academic.application.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateRequiredGradeUseCaseTest {

    private val useCase = CalculateRequiredGradeUseCase()

    @Test
    fun `when already passed, isAlreadyPassed is true`() {
        // e.g. current weighted sum is 3.2 with 60% evaluated, target 3.0
        val result = useCase(currentWeightedSum = 3.2, evaluatedWeight = 0.6, targetGrade = 3.0)
        assertTrue(result.isAlreadyPassed)
        assertTrue(result.isPossible)
    }

    @Test
    fun `calculates needed grade accurately`() {
        // 50% evaluated with sum of 1.5, target is 3.0. Remaining weight is 0.5. Needed = (3.0 - 1.5) / 0.5 = 3.0
        val result = useCase(currentWeightedSum = 1.5, evaluatedWeight = 0.5, targetGrade = 3.0)
        assertEquals(3.0, result.requiredGrade, 0.001)
        assertTrue(result.isPossible)
        assertFalse(result.isAlreadyPassed)
    }

    @Test
    fun `when needed grade exceeds 5_0, isPossible is false`() {
        // 80% evaluated with sum of 1.0, target 3.0. Remaining = 0.2. Needed = 2.0 / 0.2 = 10.0 > 5.0
        val result = useCase(currentWeightedSum = 1.0, evaluatedWeight = 0.8, targetGrade = 3.0)
        assertEquals(10.0, result.requiredGrade, 0.001)
        assertFalse(result.isPossible)
        assertFalse(result.isAlreadyPassed)
    }

    @Test
    fun `when fully evaluated and passed`() {
        val result = useCase(currentWeightedSum = 3.5, evaluatedWeight = 1.0, targetGrade = 3.0)
        assertTrue(result.isAlreadyPassed)
        assertTrue(result.isPossible)
        assertEquals(0.0, result.requiredGrade, 0.001)
    }

    @Test
    fun `when fully evaluated and failed`() {
        val result = useCase(currentWeightedSum = 2.8, evaluatedWeight = 1.0, targetGrade = 3.0)
        assertFalse(result.isAlreadyPassed)
        assertFalse(result.isPossible)
        assertEquals(0.0, result.requiredGrade, 0.001)
    }
}
