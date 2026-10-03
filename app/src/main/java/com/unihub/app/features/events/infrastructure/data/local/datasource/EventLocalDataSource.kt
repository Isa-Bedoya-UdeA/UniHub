package com.unihub.app.features.events.infrastructure.data.local.datasource

import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.local.dao.EventReminderDao
import com.unihub.app.features.events.infrastructure.data.local.entity.EventEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.EventReminderEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.EventTagEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceDayEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceRuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventLocalDataSource @Inject constructor(
    private val eventDao: EventDao,
    private val eventReminderDao: EventReminderDao
) {
    fun getEvents(userId: String): Flow<List<EventEntity>> =
        eventDao.getEvents(userId)

    fun getAllEvents(userId: String): Flow<List<EventEntity>> =
        eventDao.getAllEvents(userId)

    fun getEventById(id: String): Flow<EventEntity?> =
        eventDao.getEventById(id)

    suspend fun insertEvent(event: EventEntity) =
        eventDao.insertEvent(event)

    suspend fun deleteEvent(id: String) =
        eventDao.deleteEvent(id)

    suspend fun getEventsBySubjectOnce(subjectId: String): List<EventEntity> =
        eventDao.getEventsBySubjectOnce(subjectId)

    suspend fun deleteEventsBySubject(subjectId: String) =
        eventDao.deleteEventsBySubject(subjectId)

    fun getRemindersForEvent(eventId: String): Flow<List<EventReminderEntity>> =
        eventReminderDao.getRemindersForEvent(eventId)

    fun getAllEnabledReminders(): Flow<List<EventReminderEntity>> =
        eventReminderDao.getAllEnabledReminders()

    suspend fun insertReminders(reminders: List<EventReminderEntity>) =
        eventReminderDao.insertReminders(reminders)

    suspend fun insertReminder(reminder: EventReminderEntity) =
        eventReminderDao.insertReminder(reminder)

    suspend fun deleteRemindersForEvent(eventId: String) =
        eventReminderDao.deleteRemindersForEvent(eventId)

    fun getRecurrenceRuleById(id: String): Flow<RecurrenceRuleEntity?> =
        eventDao.getRecurrenceRuleById(id)

    suspend fun insertRecurrenceRule(rule: RecurrenceRuleEntity) =
        eventDao.insertRecurrenceRule(rule)

    suspend fun deleteRecurrenceRule(id: String) =
        eventDao.deleteRecurrenceRule(id)

    fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDayEntity>> =
        eventDao.getRecurrenceDays(ruleId)

    suspend fun insertRecurrenceDay(day: RecurrenceDayEntity) =
        eventDao.insertRecurrenceDay(day)

    suspend fun deleteRecurrenceDaysByRule(ruleId: String) =
        eventDao.deleteRecurrenceDaysByRule(ruleId)

    // Event Tags
    fun getEventTags(eventId: String): Flow<List<EventTagEntity>> =
        eventDao.getTagsByEvent(eventId)

    suspend fun insertEventTag(eventTag: EventTagEntity) =
        eventDao.insertEventTag(eventTag)

    suspend fun deleteEventTag(eventId: String, tagId: String) =
        eventDao.deleteEventTag(eventId, tagId)

    suspend fun deleteEventTagsForEvent(eventId: String) =
        eventDao.deleteEventTagsForEvent(eventId)
}
