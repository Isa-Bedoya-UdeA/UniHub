package com.unihub.app.features.academic.domain.model

data class AcademicPeriod(
    val id: String,
    val userId: String,
    val studyId: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val isCurrent: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class Grade(
    val id: String,
    val userId: String,
    val subjectId: String,
    val academicPeriodId: String,
    val name: String,
    val value: Double,
    val weight: Double,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)

data class AcademicSummary(
    val cumulativeGpa: Double,
    val currentSemesterGpa: Double,
    val earnedCredits: Int,
    val targetCredits: Int,
    val progressPercentage: Double,
    val hasManualData: Boolean = false
)
