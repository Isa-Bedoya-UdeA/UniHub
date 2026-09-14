package com.unihub.app.features.tasks.infrastructure.repository

import com.unihub.app.features.events.domain.model.EventTag
import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.mapper.toDomain
import com.unihub.app.features.events.infrastructure.data.mapper.toEntity
import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.TaskTag
import com.unihub.app.features.tasks.domain.repository.TagRepository
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TagDao
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.infrastructure.data.mapper.toDomain
import com.unihub.app.features.tasks.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val taskDao: TaskDao,
    private val eventDao: EventDao
) : TagRepository {

    override fun getTagsByUser(userId: String): Flow<List<Tag>> =
        tagDao.getTagsByUser(userId).map { entities -> entities.map { it.toDomain() } }

    override fun getTagById(id: String): Flow<Tag?> =
        tagDao.getTagById(id).map { it?.toDomain() }

    override suspend fun saveTag(tag: Tag) {
        tagDao.insertTag(tag.toEntity())
    }

    override suspend fun deleteTag(id: String) {
        tagDao.deleteTagById(id)
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
}
