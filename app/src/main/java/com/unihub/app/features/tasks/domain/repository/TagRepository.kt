package com.unihub.app.features.tasks.domain.repository

import com.unihub.app.features.tasks.domain.model.Tag
import com.unihub.app.features.tasks.domain.model.TaskTag
import com.unihub.app.features.events.domain.model.EventTag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getTagsByUser(userId: String): Flow<List<Tag>>
    fun getTagById(id: String): Flow<Tag?>
    suspend fun saveTag(tag: Tag)
    suspend fun deleteTag(id: String)
    
    // Task Associations
    fun getTagsByTask(taskId: String): Flow<List<TaskTag>>
    suspend fun addTaskTag(taskTag: TaskTag)
    suspend fun removeTaskTag(taskId: String, tagId: String)
    
    // Event Associations
    fun getTagsByEvent(eventId: String): Flow<List<EventTag>>
    suspend fun addEventTag(eventTag: EventTag)
    suspend fun removeEventTag(eventId: String, tagId: String)
}
