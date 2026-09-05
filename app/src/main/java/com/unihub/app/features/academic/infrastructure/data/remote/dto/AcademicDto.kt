package com.unihub.app.features.academic.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcademicPeriodDto(
    val id: String = "",
    val name: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val isCurrent: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class GradeDto(
    val id: String = "",
    val subjectId: String = "",
    val academicPeriodId: String = "",
    val name: String = "",
    val value: Double = 0.0,
    val weight: Double = 0.0,
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
