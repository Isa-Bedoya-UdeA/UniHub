package com.unihub.app.features.academic.domain.model

data class Subject(
    val id: String,
    val userId: String,
    val studyId: String,
    val academicPeriodId: String,
    val name: String,
    val code: String?,
    val credits: Int?,
    val professor: String?,
    val color: String?, // Hex string or reference
    val notes: String?,
    val isCompleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)
