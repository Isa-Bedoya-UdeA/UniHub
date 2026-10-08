package com.unihub.app.features.tasks.infrastructure.repository

import android.util.Log
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.model.TaskTag
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.infrastructure.data.local.datasource.TaskLocalDataSource
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.infrastructure.data.mapper.toDomain
import com.unihub.app.features.tasks.infrastructure.data.mapper.toDto
import com.unihub.app.features.tasks.infrastructure.data.mapper.toEntity
import com.unihub.app.features.tasks.infrastructure.data.remote.datasource.TaskRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(userId: String): Flow<List<Task>> {
        return localDataSource.getTasks(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTasksBySubject(subjectId: String): Flow<List<Task>> {
        return localDataSource.getTasksBySubject(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTaskById(id: String): Flow<Task?> {
        return localDataSource.getTaskById(id).map { it?.toDomain() }
    }

    override suspend fun saveTask(task: Task) {
        
        localDataSource.insertTask(task.toEntity())
        
        try {
            val dto = task.toDto()
            remoteDataSource.saveTask(task.userId, dto)
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error saving task to Firestore: ${e.message}", e)
        }
    }

    override suspend fun updateTask(task: Task) {
        
        localDataSource.insertTask(task.toEntity())
        
        try {
            val dto = task.toDto()
            remoteDataSource.saveTask(task.userId, dto)
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error updating task in Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteTask(id: String) {
        val task = localDataSource.getTaskById(id).firstOrNull()
        localDataSource.deleteTask(id)
        if (task != null) {
            try {
                remoteDataSource.deleteTask(task.userId, id)
            } catch (e: Exception) {
                Log.e("TaskRepositoryImpl", "Error deleting task from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
        localDataSource.updateStatus(id, status)
        val task = localDataSource.getTaskById(id).firstOrNull() ?: return
        try {
            val dto = task.toDto()
            remoteDataSource.saveTask(task.userId, dto)
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error syncing task status to Firestore: ${e.message}")
        }
    }

    override suspend fun syncTasks(userId: String) {
        try {
            val remoteTasks = remoteDataSource.getTasks(userId)
            remoteTasks.forEach { dto ->
                localDataSource.insertTask(dto.toDomain(userId).toEntity())
                
                // Sync task tags from Firestore subcollection
                val remoteTagIds = remoteDataSource.getTaskTags(userId, dto.id)
                taskDao.deleteTaskTagsForTask(dto.id)
                remoteTagIds.forEach { tagId ->
                    val taskTag = TaskTag(taskId = dto.id, tagId = tagId)
                    taskDao.insertTaskTag(taskTag.toEntity())
                }
            }
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error syncing tasks from Firestore: ${e.message}")
        }
    }

    override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {
        try {
            // Save to Room
            taskDao.deleteTaskTagsForTask(taskId)
            tagIds.forEach { tagId ->
                val taskTag = TaskTag(taskId = taskId, tagId = tagId)
                taskDao.insertTaskTag(taskTag.toEntity())
            }
            
            // Save to Firestore
            remoteDataSource.updateTaskTags(userId, taskId, tagIds)
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error saving task tags: ${e.message}")
        }
    }

    override suspend fun getTaskTags(userId: String, taskId: String): List<String> {
        return try {
            taskDao.getTagsByTask(taskId).first().map { it.tagId }
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error getting task tags: ${e.message}")
            emptyList()
        }
    }
}
