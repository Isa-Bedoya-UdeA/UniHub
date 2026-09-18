package com.unihub.app.features.events.domain.repository

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(userId: String, start: String, end: String): Flow<List<Event>>
    fun getAllEvents(userId: String): Flow<List<Event>>
    fun getEventById(id: String): Flow<Event?>
    suspend fun saveEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(id: String)
    suspend fun syncEvents(userId: String)

    fun getRemindersForEvent(eventId: String): Flow<List<EventReminder>>
    suspend fun saveReminders(eventId: String, reminders: List<EventReminder>)
    suspend fun deleteRemindersForEvent(eventId: String)
    fun getAllEnabledReminders(): Flow<List<EventReminder>>

    fun getRecurrenceRule(ruleId: String): Flow<RecurrenceRule?>
    fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDay>>
    suspend fun saveRecurrenceRule(rule: RecurrenceRule)
    suspend fun saveRecurrenceDay(day: RecurrenceDay)
    suspend fun deleteRecurrenceRule(id: String)
    suspend fun deleteRecurrenceDaysByRule(ruleId: String)
}
