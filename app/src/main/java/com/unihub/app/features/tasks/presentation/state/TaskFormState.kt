package com.unihub.app.features.tasks.presentation.state

import com.unihub.app.features.tasks.domain.model.TaskPriority

data class TaskFormState(
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val dueDate: String = "",
    val dueDateError: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val reminderAt: String? = null,
    val isDeadlineReminderEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
