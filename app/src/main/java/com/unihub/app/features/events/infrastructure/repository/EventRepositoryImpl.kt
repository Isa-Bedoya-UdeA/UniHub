package com.unihub.app.features.events.infrastructure.repository

import android.util.Log
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventTag
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.events.infrastructure.data.local.datasource.EventLocalDataSource
import com.unihub.app.features.events.infrastructure.data.mapper.toDomain
import com.unihub.app.features.events.infrastructure.data.mapper.toDto
import com.unihub.app.features.events.infrastructure.data.mapper.toEntity
import com.unihub.app.features.events.infrastructure.data.remote.datasource.EventRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val localDataSource: EventLocalDataSource,
    private val remoteDataSource: EventRemoteDataSource
) : EventRepository {

    override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> =
        localDataSource.getEvents(userId).map { entities ->
            entities.map { entity ->
                val reminders = localDataSource.getRemindersForEvent(entity.id).first()
                    .map { it.toDomain() }

                var rule: RecurrenceRule? = null
                var days: List<Int> = emptyList()

                entity.recurrenceRuleId?.let { ruleId ->
                    rule = localDataSource.getRecurrenceRuleById(ruleId).first()?.toDomain()
                    days = localDataSource.getRecurrenceDays(ruleId).first().map { it.dayOfWeek }
                }

                entity.toDomain(reminders, rule, days)
            }
        }

    override fun getAllEvents(userId: String): Flow<List<Event>> =
        localDataSource.getAllEvents(userId).map { entities ->
            entities.map { entity ->
                val reminders = localDataSource.getRemindersForEvent(entity.id).first()
                    .map { it.toDomain() }

                var rule: RecurrenceRule? = null
                var days: List<Int> = emptyList()

                entity.recurrenceRuleId?.let { ruleId ->
                    rule = localDataSource.getRecurrenceRuleById(ruleId).first()?.toDomain()
                    days = localDataSource.getRecurrenceDays(ruleId).first().map { it.dayOfWeek }
                }

                entity.toDomain(reminders, rule, days)
            }
        }

    override fun getEventById(id: String): Flow<Event?> =
        localDataSource.getEventById(id).map { entity ->
            entity?.let {
                val reminders = localDataSource.getRemindersForEvent(it.id).first()
                    .map { reminder -> reminder.toDomain() }

                var rule: RecurrenceRule? = null
                var days: List<Int> = emptyList()

                it.recurrenceRuleId?.let { ruleId ->
                    rule = localDataSource.getRecurrenceRuleById(ruleId).first()?.toDomain()
                    days = localDataSource.getRecurrenceDays(ruleId).first().map { it.dayOfWeek }
                }

                it.toDomain(reminders, rule, days)
            }
        }

    override suspend fun saveEvent(event: Event) {
        Log.d("FIRESTORE_DEBUG", "=== SAVE EVENT CALLED ===")
        Log.d("FIRESTORE_DEBUG", "Event ID: ${event.id}")
        Log.d("FIRESTORE_DEBUG", "Event Title: ${event.title}")
        Log.d("FIRESTORE_DEBUG", "User ID: ${event.userId}")
        
        localDataSource.insertEvent(event.toEntity())
        Log.d("FIRESTORE_DEBUG", "Event saved to Room successfully")
        
        try {
            Log.d("FIRESTORE_DEBUG", "About to call remoteDataSource.saveEvent()")
            val dto = event.toDto()
            Log.d("FIRESTORE_DEBUG", "DTO created: $dto")
            remoteDataSource.saveEvent(event.userId, dto)
            Log.d("FIRESTORE_DEBUG", "remoteDataSource.saveEvent() completed successfully")
        } catch (e: Exception) {
            Log.e("FIRESTORE_DEBUG", "ERROR saving event to Firestore: ${e.message}", e)
            Log.e("FIRESTORE_DEBUG", "Exception type: ${e.javaClass.simpleName}")
            Log.e("FIRESTORE_DEBUG", "Stack trace: ${e.stackTraceToString()}")
        }
    }

    override suspend fun updateEvent(event: Event) {
        Log.d("FIRESTORE_DEBUG", "=== UPDATE EVENT CALLED ===")
        Log.d("FIRESTORE_DEBUG", "Event ID: ${event.id}")
        Log.d("FIRESTORE_DEBUG", "Event Title: ${event.title}")
        Log.d("FIRESTORE_DEBUG", "User ID: ${event.userId}")
        
        localDataSource.insertEvent(event.toEntity())
        Log.d("FIRESTORE_DEBUG", "Event updated in Room successfully")
        
        try {
            Log.d("FIRESTORE_DEBUG", "About to call remoteDataSource.saveEvent() for update")
            val dto = event.toDto()
            Log.d("FIRESTORE_DEBUG", "DTO created: $dto")
            remoteDataSource.saveEvent(event.userId, dto)
            Log.d("FIRESTORE_DEBUG", "remoteDataSource.saveEvent() completed successfully")
        } catch (e: Exception) {
            Log.e("FIRESTORE_DEBUG", "ERROR updating event in Firestore: ${e.message}", e)
            Log.e("FIRESTORE_DEBUG", "Exception type: ${e.javaClass.simpleName}")
            Log.e("FIRESTORE_DEBUG", "Stack trace: ${e.stackTraceToString()}")
        }
    }

    override suspend fun deleteEvent(id: String) {
        localDataSource.deleteRemindersForEvent(id)
        val event = localDataSource.getEventById(id).first()
        event?.recurrenceRuleId?.let { ruleId ->
            localDataSource.deleteRecurrenceDaysByRule(ruleId)
            localDataSource.deleteRecurrenceRule(ruleId)
        }
        localDataSource.deleteEvent(id)
        event?.let {
            try {
                remoteDataSource.deleteEvent(it.userId, id)
            } catch (e: Exception) {
                Log.e("EventRepositoryImpl", "Error deleting event from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun syncEvents(userId: String) {
        try {
            val remoteEvents = remoteDataSource.getEvents(userId)
            remoteEvents.forEach { dto ->
                localDataSource.insertEvent(dto.toDomain(userId).toEntity())
                
                // Sync reminders
                val remoteReminders = remoteDataSource.getRemindersForEvent(userId, dto.id)
                localDataSource.deleteRemindersForEvent(dto.id)
                remoteReminders.forEach { reminderData ->
                    val reminderId = reminderData["id"] as? String ?: return@forEach
                    val reminderTypeStr = reminderData["reminderType"] as? String ?: return@forEach
                    val value = (reminderData["value"] as? Number)?.toInt() ?: return@forEach
                    val isEnabled = reminderData["isEnabled"] as? Boolean ?: true
                    
                    val reminderType = try {
                        com.unihub.app.features.events.domain.model.ReminderType.valueOf(reminderTypeStr)
                    } catch (e: Exception) {
                        return@forEach
                    }
                    
                    val reminderEntity = com.unihub.app.features.events.infrastructure.data.local.entity.EventReminderEntity(
                        id = reminderId,
                        eventId = dto.id,
                        reminderType = reminderType,
                        value = value,
                        isEnabled = isEnabled
                    )
                    localDataSource.insertReminder(reminderEntity)
                }
                
                // Sync event tags
                val remoteEventTags = remoteDataSource.getEventTags(userId, dto.id)
                localDataSource.deleteEventTagsForEvent(dto.id)
                remoteEventTags.forEach { tagId ->
                    val eventTagEntity = com.unihub.app.features.events.infrastructure.data.local.entity.EventTagEntity(
                        eventId = dto.id,
                        tagId = tagId
                    )
                    localDataSource.insertEventTag(eventTagEntity)
                }
            }
            
            val remoteRules = remoteDataSource.getRecurrenceRules(userId)
            remoteRules.forEach { ruleData ->
                val ruleId = ruleData["id"] as? String ?: return@forEach
                val frequency = ruleData["frequency"] as? String ?: "WEEKLY"
                val interval = (ruleData["interval"] as? Number)?.toInt() ?: 1
                val startDate = ruleData["startDate"] as? String ?: ""
                val endDate = ruleData["endDate"] as? String ?: ""
                
                val ruleEntity = com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceRuleEntity(
                    id = ruleId,
                    userId = userId,
                    frequency = frequency,
                    interval = interval,
                    startDate = startDate,
                    endDate = endDate,
                    createdAt = ruleData["createdAt"] as? String ?: "",
                    updatedAt = ruleData["updatedAt"] as? String ?: ""
                )
                localDataSource.insertRecurrenceRule(ruleEntity)
                
                val remoteDays = remoteDataSource.getRecurrenceDays(userId, ruleId)
                remoteDays.forEach { dayData ->
                    val dayOfWeek = (dayData["dayOfWeek"] as? Number)?.toInt() ?: return@forEach
                    val dayEntity = com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceDayEntity(
                        recurrenceRuleId = ruleId,
                        dayOfWeek = dayOfWeek
                    )
                    localDataSource.insertRecurrenceDay(dayEntity)
                }
            }
        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Error syncing events from Firestore: ${e.message}")
        }
    }

    override fun getRemindersForEvent(eventId: String): Flow<List<EventReminder>> =
        localDataSource.getRemindersForEvent(eventId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveReminders(eventId: String, reminders: List<EventReminder>) {
        localDataSource.deleteRemindersForEvent(eventId)
        localDataSource.insertReminders(reminders.map { it.toEntity() })
        
        try {
            val userId = localDataSource.getEventById(eventId).first()?.userId ?: return
            remoteDataSource.deleteRemindersForEvent(userId, eventId)
            
            reminders.forEach { reminder ->
                val data = mapOf(
                    "id" to reminder.id,
                    "eventId" to reminder.eventId,
                    "reminderType" to reminder.reminderType.name,
                    "value" to reminder.value,
                    "isEnabled" to reminder.isEnabled
                )
                remoteDataSource.saveReminder(userId, eventId, reminder.id, data)
            }
        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Error saving reminders to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteRemindersForEvent(eventId: String) {
        localDataSource.deleteRemindersForEvent(eventId)
    }

    override fun getAllEnabledReminders(): Flow<List<EventReminder>> =
        localDataSource.getAllEnabledReminders().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRecurrenceRule(ruleId: String): Flow<RecurrenceRule?> =
        localDataSource.getRecurrenceRuleById(ruleId).map { it?.toDomain() }

    override fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDay>> =
        localDataSource.getRecurrenceDays(ruleId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveRecurrenceRule(rule: RecurrenceRule) {
        localDataSource.insertRecurrenceRule(rule.toEntity())
        
        try {
            val data = mapOf(
                "id" to rule.id,
                "userId" to rule.userId,
                "frequency" to rule.frequency,
                "interval" to rule.interval,
                "startDate" to rule.startDate,
                "endDate" to rule.endDate,
                "createdAt" to rule.createdAt,
                "updatedAt" to rule.updatedAt
            )
            remoteDataSource.saveRecurrenceRule(rule.userId, rule.id, data)
        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Error saving recurrence rule to Firestore: ${e.message}", e)
        }
    }

    override suspend fun saveRecurrenceDay(day: RecurrenceDay) {
        localDataSource.insertRecurrenceDay(day.toEntity())
        
        try {
            val dayId = "${day.recurrenceRuleId}_${day.dayOfWeek}"
            val data = mapOf(
                "recurrenceRuleId" to day.recurrenceRuleId,
                "dayOfWeek" to day.dayOfWeek
            )
            val userId = localDataSource.getRecurrenceRuleById(day.recurrenceRuleId).first()?.userId ?: return
            remoteDataSource.saveRecurrenceDay(userId, day.recurrenceRuleId, dayId, data)
        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Error saving recurrence day to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteRecurrenceRule(id: String) {
        localDataSource.deleteRecurrenceRule(id)
    }

    override suspend fun deleteRecurrenceDaysByRule(ruleId: String) {
        localDataSource.deleteRecurrenceDaysByRule(ruleId)
    }

    override fun getEventTags(eventId: String): Flow<List<EventTag>> =
        localDataSource.getEventTags(eventId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveEventTag(eventTag: EventTag) {
        localDataSource.insertEventTag(eventTag.toEntity())
        
        try {
            val userId = localDataSource.getEventById(eventTag.eventId).first()?.userId ?: return
            remoteDataSource.saveEventTag(userId, eventTag.eventId, eventTag.tagId)
        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Error saving event tag to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteEventTag(eventId: String, tagId: String) {
        localDataSource.deleteEventTag(eventId, tagId)
        
        try {
            val userId = localDataSource.getEventById(eventId).first()?.userId ?: return
            remoteDataSource.deleteEventTag(userId, eventId, tagId)
        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Error deleting event tag from Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteEventTagsForEvent(eventId: String) {
        localDataSource.deleteEventTagsForEvent(eventId)
    }
}
