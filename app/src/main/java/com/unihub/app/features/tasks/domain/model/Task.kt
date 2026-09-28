package com.unihub.app.features.tasks.domain.model

enum class TaskPriority { LOW, MEDIUM, HIGH }
enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED }

enum class TaskReminderType {
    MINUTES_BEFORE,
    HOURS_BEFORE,
    DAYS_BEFORE
}

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
    val reminderType: TaskReminderType?,
    val reminderValue: Int?,
    val isDeadlineReminderEnabled: Boolean,
    val createdAt: String,
    val updatedAt: String
)
