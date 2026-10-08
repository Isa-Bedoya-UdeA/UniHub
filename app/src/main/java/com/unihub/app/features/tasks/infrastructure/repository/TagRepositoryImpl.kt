package com.unihub.app.features.tasks.infrastructure.repository

import android.util.Log
import com.unihub.app.features.events.domain.model.EventTag
import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.mapper.toDomain
import com.unihub.app.features.events.infrastructure.data.mapper.toEntity
import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.TaskTag
import com.unihub.app.features.tasks.domain.repository.TagRepository
import com.unihub.app.features.tasks.infrastructure.data.local.datasource.TagLocalDataSource
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.infrastructure.data.mapper.toDomain
import com.unihub.app.features.tasks.infrastructure.data.mapper.toDto
import com.unihub.app.features.tasks.infrastructure.data.mapper.toEntity
import com.unihub.app.features.tasks.infrastructure.data.remote.datasource.TagRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val localDataSource: TagLocalDataSource,
    private val remoteDataSource: TagRemoteDataSource,
    private val taskDao: TaskDao,
    private val eventDao: EventDao
) : TagRepository {

    override fun getTagsByUser(userId: String): Flow<List<Tag>> =
        localDataSource.getTagsByUser(userId).map { entities -> entities.map { it.toDomain() } }

    override fun getTagById(id: String): Flow<Tag?> =
        localDataSource.getTagById(id).map { it?.toDomain() }

    override suspend fun saveTag(tag: Tag) {
        localDataSource.insertTag(tag.toEntity())
        
        try {
            val dto = tag.toDto()
            remoteDataSource.saveTag(tag.userId, dto)
        } catch (e: Exception) {
            Log.e("TagRepositoryImpl", "Error saving tag to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteTag(id: String) {
        val tag = localDataSource.getTagById(id).firstOrNull()
        localDataSource.deleteTagById(id)
        tag?.let {
            try {
                remoteDataSource.deleteTag(it.userId, id)
            } catch (e: Exception) {
                Log.e("TagRepositoryImpl", "Error deleting tag from Firestore: ${e.message}")
            }
        }
    }

    override fun getTagsByTask(taskId: String): Flow<List<TaskTag>> =
        taskDao.getTagsByTask(taskId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addTaskTag(taskTag: TaskTag) {
        taskDao.insertTaskTag(taskTag.toEntity())
    }

    override suspend fun removeTaskTag(taskId: String, tagId: String) {
        taskDao.deleteTaskTag(taskId, tagId)
    }

    override fun getTagsByEvent(eventId: String): Flow<List<EventTag>> =
        eventDao.getTagsByEvent(eventId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addEventTag(eventTag: EventTag) {
        eventDao.insertEventTag(eventTag.toEntity())
    }

    override suspend fun removeEventTag(eventId: String, tagId: String) {
        eventDao.deleteEventTag(eventId, tagId)
    }

    override suspend fun syncTags(userId: String) {
        try {
            val remoteTags = remoteDataSource.getTags(userId)
            remoteTags.forEach { dto ->
                localDataSource.insertTag(dto.toDomain().toEntity())
            }
        } catch (e: Exception) {
            Log.e("TagRepositoryImpl", "Error syncing tags from Firestore: ${e.message}")
        }
    }
}
