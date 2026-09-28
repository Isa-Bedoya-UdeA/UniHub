package com.unihub.app.features.tasks.infrastructure.data.mapper

import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.infrastructure.data.remote.dto.TaskDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskMapperTest {

    @Test
    fun `Task toEntity and back preserves data`() {
        val task = Task(
            id = "t1",
            userId = "u1",
            academicPeriodId = "p1",
            subjectId = "s1",
            title = "Test Task",
            description = "Description",
            dueAt = "2025-06-01",
            priority = TaskPriority.HIGH,
            status = TaskStatus.PENDING,
            notes = "Notes",
            reminderAt = null,
            isDeadlineReminderEnabled = false,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val entity = task.toEntity()
        val restored = entity.toDomain()

        assertEquals(task, restored)
    }

    @Test
    fun `Task toDto and back preserves core fields`() {
        val task = Task(
            id = "t1",
            userId = "u1",
            academicPeriodId = "p1",
            subjectId = "s1",
            title = "Test Task",
            description = "Desc",
            dueAt = null,
            priority = TaskPriority.LOW,
            status = TaskStatus.COMPLETED,
            notes = null,
            reminderAt = null,
            isDeadlineReminderEnabled = false,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val dto = task.toDto()
        assertEquals("t1", dto.id)
        assertEquals("LOW", dto.priority)
        assertEquals("COMPLETED", dto.status)
        assertNull(dto.dueAt)
    }

    @Test
    fun `TaskDto toDomain maps enums correctly`() {
        val dto = TaskDto(
            id = "t1",
            academicPeriodId = null,
            subjectId = null,
            title = "Test",
            description = null,
            dueAt = null,
            priority = "HIGH",
            status = "IN_PROGRESS",
            notes = null,
            createdAt = 0L,
            updatedAt = 0L
        )

        val task = dto.toDomain("u1")

        assertEquals(TaskPriority.HIGH, task.priority)
        assertEquals(TaskStatus.IN_PROGRESS, task.status)
        assertEquals("u1", task.userId)
    }

    @Test
    fun `TaskDto toDomain handles invalid enum gracefully`() {
        val dto = TaskDto(
            id = "t1",
            priority = "INVALID",
            status = "INVALID",
            title = "Test",
            createdAt = 0L,
            updatedAt = 0L
        )

        val task = dto.toDomain("u1")

        assertEquals(TaskPriority.MEDIUM, task.priority)
        assertEquals(TaskStatus.PENDING, task.status)
    }

    @Test
    fun `Tag toDto and back preserves data`() {
        val tag = Tag(
            id = "tag1",
            userId = "u1",
            name = "Important",
            createdAt = "2025-01-01"
        )

        val dto = tag.toDto()
        val restored = dto.toDomain()

        assertEquals(tag, restored)
    }
}
