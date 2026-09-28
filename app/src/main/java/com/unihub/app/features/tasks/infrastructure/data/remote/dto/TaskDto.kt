package com.unihub.app.features.tasks.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String = "",
    val academicPeriodId: String? = null,
    val subjectId: String? = null,
    val title: String = "",
    val description: String? = null,
    val dueAt: String? = null,
    val priority: String = "MEDIUM",
    val status: String = "PENDING",
    val reminderType: String? = null,
    val reminderValue: Int? = null,
    val isDeadlineReminderEnabled: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)
