package com.unihub.app.features.tasks.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String = "",
    val academicPeriodId: String? = null,
    val subjectId: String? = null,
    val title: String = "",
    val description: String? = null,
    val dueAt: Long? = null,
    val priority: String = "MEDIUM",
    val status: String = "PENDING",
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
