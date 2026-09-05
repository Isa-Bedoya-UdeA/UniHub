package com.unihub.app.features.events.domain.repository

import com.unihub.app.features.events.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(userId: String, start: String, end: String): Flow<List<Event>>
    fun getEventById(id: String): Flow<Event?>
    suspend fun saveEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(id: String)
    suspend fun syncEvents(userId: String)
}
