package com.unihub.app.features.events.infrastructure.repository

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.local.dao.EventReminderDao
import com.unihub.app.features.events.infrastructure.data.mapper.toDomain
import com.unihub.app.features.events.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val eventReminderDao: EventReminderDao
) : EventRepository {

    override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> =
        eventDao.getEvents(userId, start, end).map { entities ->
            entities.map { entity ->
                val reminders = eventReminderDao.getRemindersForEvent(entity.id).first()
                    .map { it.toDomain() }
                entity.toDomain(reminders)
            }
        }

    override fun getAllEvents(userId: String): Flow<List<Event>> =
        eventDao.getAllEvents(userId).map { entities ->
            entities.map { entity ->
                val reminders = eventReminderDao.getRemindersForEvent(entity.id).first()
                    .map { it.toDomain() }
                entity.toDomain(reminders)
            }
        }

    override fun getEventById(id: String): Flow<Event?> =
        eventDao.getEventById(id).map { entity ->
            entity?.let {
                val reminders = eventReminderDao.getRemindersForEvent(it.id).first()
                    .map { reminder -> reminder.toDomain() }
                it.toDomain(reminders)
            }
        }

    override suspend fun saveEvent(event: Event) {
        eventDao.insertEvent(event.toEntity())
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.insertEvent(event.toEntity())
    }

    override suspend fun deleteEvent(id: String) {
        eventReminderDao.deleteRemindersForEvent(id)
        eventDao.deleteEvent(id)
    }

    override suspend fun syncEvents(userId: String) {}

    override fun getRemindersForEvent(eventId: String): Flow<List<EventReminder>> =
        eventReminderDao.getRemindersForEvent(eventId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveReminders(eventId: String, reminders: List<EventReminder>) {
        eventReminderDao.deleteRemindersForEvent(eventId)
        eventReminderDao.insertReminders(reminders.map { it.toEntity() })
    }

    override suspend fun deleteRemindersForEvent(eventId: String) {
        eventReminderDao.deleteRemindersForEvent(eventId)
    }

    override fun getAllEnabledReminders(): Flow<List<EventReminder>> =
        eventReminderDao.getAllEnabledReminders().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRecurrenceRule(ruleId: String): Flow<RecurrenceRule?> =
        eventDao.getRecurrenceRuleById(ruleId).map { it?.toDomain() }

    override fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDay>> =
        eventDao.getRecurrenceDays(ruleId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveRecurrenceRule(rule: RecurrenceRule) {
        eventDao.insertRecurrenceRule(rule.toEntity())
    }

    override suspend fun saveRecurrenceDay(day: RecurrenceDay) {
        eventDao.insertRecurrenceDay(day.toEntity())
    }
}
