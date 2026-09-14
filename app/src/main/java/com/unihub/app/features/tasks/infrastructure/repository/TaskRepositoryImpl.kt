package com.unihub.app.features.tasks.infrastructure.repository

import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.infrastructure.data.mapper.toDomain
import com.unihub.app.features.tasks.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(userId: String): Flow<List<Task>> {
        return taskDao.getTasks(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTasksBySubject(subjectId: String): Flow<List<Task>> {
        return taskDao.getTasksBySubject(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTaskById(id: String): Flow<Task?> {
        return taskDao.getTaskById(id).map { it?.toDomain() }
    }

    override suspend fun saveTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteTask(id)
    }

    override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
        taskDao.updateStatus(id, status)
    }

    override suspend fun syncTasks(userId: String) {
        // To be implemented
    }
}
