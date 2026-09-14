package com.unihub.app.features.tasks.infrastructure.data.mapper

import com.unihub.app.features.tasks.domain.model.*
import com.unihub.app.features.tasks.infrastructure.data.local.entity.*
import com.unihub.app.features.tasks.infrastructure.data.remote.dto.TaskDto

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        status = status,
        notes = notes,
        reminderAt = reminderAt,
        isDeadlineReminderEnabled = isDeadlineReminderEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority,
        status = status,
        notes = notes,
        reminderAt = reminderAt,
        isDeadlineReminderEnabled = isDeadlineReminderEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Tag.toEntity(): TagEntity {
    return TagEntity(
        id = id,
        userId = userId,
        name = name,
        createdAt = createdAt
    )
}

fun TagEntity.toDomain(): Tag {
    return Tag(
        id = id,
        userId = userId,
        name = name,
        createdAt = createdAt
    )
}

fun TaskTag.toEntity(): TaskTagEntity {
    return TaskTagEntity(
        taskId = taskId,
        tagId = tagId
    )
}

fun TaskTagEntity.toDomain(): TaskTag {
    return TaskTag(
        taskId = taskId,
        tagId = tagId
    )
}


fun TaskDto.toDomain(userId: String): Task {
    return Task(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        title = title,
        description = description,
        dueAt = dueAt?.toString(),
        priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.MEDIUM },
        status = try { TaskStatus.valueOf(status) } catch (e: Exception) { TaskStatus.PENDING },
        notes = notes,
        reminderAt = null,
        isDeadlineReminderEnabled = false,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        title = title,
        description = description,
        dueAt = dueAt?.toLongOrNull(),
        priority = priority.name,
        status = status.name,
        notes = notes,
        createdAt = createdAt.toLongOrNull() ?: 0L,
        updatedAt = updatedAt.toLongOrNull() ?: 0L
    )
}
