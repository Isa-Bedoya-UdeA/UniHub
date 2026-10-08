package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class AcademicSummaryResponse(
    val uid: String,
    val activeProgram: String? = null,
    val activePeriod: String? = null,
    val approvedCredits: Int = 0,
    val totalCredits: Int = 0,
    val cumulativeGpa: Double = 0.0,
    val subjectsCount: Int = 0
)

@Serializable
data class AcademicCalculateRequest(
    val currentGrade: Double,
    val evaluatedWeight: Double,
    val targetGrade: Double
)

@Serializable
data class AcademicCalculateResponse(
    val currentGrade: Double,
    val evaluatedWeight: Double,
    val remainingWeight: Double,
    val targetGrade: Double,
    val requiredGrade: Double,
    val isAchievable: Boolean,
    val message: String
)
