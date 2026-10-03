package com.unihub.app.features.tasks.presentation.state

import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskReminderType
import com.unihub.app.features.tasks.domain.model.TaskStatus

data class TaskFormState(
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val dueDate: String = "",
    val dueDateError: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val reminderType: TaskReminderType? = null,
    val reminderValue: Int? = null,
    val isDeadlineReminderEnabled: Boolean = false,
    val selectedSubjectId: String? = null,
    val availableSubjects: List<com.unihub.app.features.academic.domain.model.Subject> = emptyList(),
    val tagInput: String = "",
    val selectedTags: List<Tag> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
