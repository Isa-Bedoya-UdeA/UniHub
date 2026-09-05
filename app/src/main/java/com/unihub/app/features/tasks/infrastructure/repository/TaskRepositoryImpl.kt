package com.unihub.app.features.tasks.infrastructure.repository

import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor() : TaskRepository {

    private val mockTasks = MutableStateFlow<List<Task>>(
        listOf(
            Task(
                id = "1", userId = "user123", academicPeriodId = "2026-2",
                subjectId = "1", title = "Informe Final de Investigación",
                description = "Revisión bibliográfica completa", dueAt = "15-08-2026",
                priority = TaskPriority.HIGH, status = TaskStatus.PENDING,
                notes = null, createdAt = "", updatedAt = ""
            ),
            Task(
                id = "2", userId = "user123", academicPeriodId = "2026-2",
                subjectId = "2", title = "Quiz de Normalización",
                description = "3ra forma normal", dueAt = "20-08-2026",
                priority = TaskPriority.MEDIUM, status = TaskStatus.COMPLETED,
                notes = null, createdAt = "", updatedAt = ""
            )
        )
    )

    override fun getTasks(userId: String): Flow<List<Task>> = mockTasks.map { it.filter { t -> t.userId == userId } }

    override fun getTasksBySubject(subjectId: String): Flow<List<Task>> = mockTasks.map { it.filter { t -> t.subjectId == subjectId } }

    override fun getTaskById(id: String): Flow<Task?> = mockTasks.map { it.find { t -> t.id == id } }

    override suspend fun saveTask(task: Task) {
        val current = mockTasks.value.toMutableList()
        val index = current.indexOfFirst { it.id == task.id }
        if (index != -1) current[index] = task else current.add(task)
        mockTasks.emit(current)
    }

    override suspend fun updateTask(task: Task) {
        saveTask(task)
    }

    override suspend fun deleteTask(id: String) {
        mockTasks.emit(mockTasks.value.filter { it.id != id })
    }

    override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
        val current = mockTasks.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            current[index] = current[index].copy(status = status)
            mockTasks.emit(current)
        }
    }

    override suspend fun syncTasks(userId: String) {}
}
