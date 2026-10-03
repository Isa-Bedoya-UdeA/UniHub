package com.unihub.app.features.tasks.infrastructure.data.local.datasource

import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskLocalDataSource @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getTasks(userId: String): Flow<List<TaskEntity>> =
        taskDao.getTasks(userId)

    fun getTasksBySubject(subjectId: String): Flow<List<TaskEntity>> =
        taskDao.getTasksBySubject(subjectId)

    fun getTaskById(id: String): Flow<TaskEntity?> =
        taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity) =
        taskDao.insertTask(task)

    suspend fun updateStatus(id: String, status: TaskStatus) =
        taskDao.updateStatus(id, status)

    suspend fun deleteTask(id: String) =
        taskDao.deleteTask(id)

    suspend fun getTasksBySubjectOnce(subjectId: String): List<TaskEntity> =
        taskDao.getTasksBySubjectOnce(subjectId)

    suspend fun deleteTasksBySubject(subjectId: String) =
        taskDao.deleteTasksBySubject(subjectId)
}
