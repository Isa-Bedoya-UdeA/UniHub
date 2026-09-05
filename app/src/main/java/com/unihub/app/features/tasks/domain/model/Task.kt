package com.unihub.app.features.tasks.domain.model

enum class TaskPriority { LOW, MEDIUM, HIGH }
enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED }

data class Task(
    val id: String,
    val userId: String,
    val academicPeriodId: String?,
    val subjectId: String?,
    val title: String,
    val description: String?,
    val dueAt: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)
