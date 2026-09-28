package com.unihub.app.features.academic.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudyDto(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val institution: String = "",
    val totalCredits: Int = 0,
    val approvedCredits: Int? = null,
    val cumulativeGpa: Double? = null,
    val isActive: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class AcademicPeriodDto(
    val id: String = "",
    val userId: String = "",
    val studyId: String = "",
    val name: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isCurrent: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class GradeDto(
    val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val academicPeriodId: String = "",
    val name: String = "",
    val value: Double = 0.0,
    val weight: Double = 0.0,
    val notes: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)
