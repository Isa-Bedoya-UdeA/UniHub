package com.unihub.app.features.subjects.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
    val id: String = "",
    val academicPeriodId: String = "",
    val name: String = "",
    val code: String? = null,
    val credits: Int? = null,
    val professor: String? = null,
    val color: String? = null,
    val notes: String? = null,
    val createdAt: Long = 0L, // Using Long for timestamp in Firestore DTO is common
    val updatedAt: Long = 0L
)
