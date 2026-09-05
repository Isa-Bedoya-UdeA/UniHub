package com.unihub.app.features.tasks.domain.repository

import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(userId: String): Flow<List<Task>>
    fun getTasksBySubject(subjectId: String): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    suspend fun saveTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun updateTaskStatus(id: String, status: TaskStatus)
    suspend fun syncTasks(userId: String)
}
