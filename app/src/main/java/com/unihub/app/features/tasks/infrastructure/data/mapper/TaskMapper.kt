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
        reminderType = reminderType,
        reminderValue = reminderValue,
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
        reminderType = reminderType,
        reminderValue = reminderValue,
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
        dueAt = dueAt,
        priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.MEDIUM },
        status = try { TaskStatus.valueOf(status) } catch (e: Exception) { TaskStatus.PENDING },
        reminderType = reminderType?.let { try { TaskReminderType.valueOf(it) } catch (e: Exception) { null } },
        reminderValue = reminderValue,
        isDeadlineReminderEnabled = isDeadlineReminderEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        title = title,
        description = description,
        dueAt = dueAt,
        priority = priority.name,
        status = status.name,
        reminderType = reminderType?.name,
        reminderValue = reminderValue,
        isDeadlineReminderEnabled = isDeadlineReminderEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Tag.toDto(): com.unihub.app.features.tasks.infrastructure.data.remote.dto.TagDto {
    return com.unihub.app.features.tasks.infrastructure.data.remote.dto.TagDto(
        id = id,
        userId = userId,
        name = name,
        createdAt = createdAt
    )
}

fun com.unihub.app.features.tasks.infrastructure.data.remote.dto.TagDto.toDomain(): Tag {
    return Tag(
        id = id,
        userId = userId,
        name = name,
        createdAt = createdAt
    )
}
