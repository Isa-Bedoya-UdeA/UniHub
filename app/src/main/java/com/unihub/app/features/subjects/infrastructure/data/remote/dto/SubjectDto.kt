package com.unihub.app.features.subjects.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
    val id: String = "",
    val userId: String = "",
    val studyId: String = "",
    val academicPeriodId: String = "",
    val name: String = "",
    val code: String? = null,
    val credits: Int? = null,
    val professor: String? = null,
    val color: String? = null,
    val notes: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)
