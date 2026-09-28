package com.unihub.app.features.events.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.events.infrastructure.data.remote.dto.EventDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveEvent(userId: String, eventDto: EventDto) {
        try {
            val data = mapOf(
                "id" to eventDto.id,
                "academicPeriodId" to eventDto.academicPeriodId,
                "subjectId" to eventDto.subjectId,
                "locationId" to eventDto.locationId,
                "recurrenceRuleId" to eventDto.recurrenceRuleId,
                "title" to eventDto.title,
                "startAt" to eventDto.startAt,
                "endAt" to eventDto.endAt,
                "locationType" to eventDto.locationType,
                "eventType" to eventDto.eventType,
                "meetingUrl" to eventDto.meetingUrl,
                "notes" to eventDto.notes,
                "createdAt" to eventDto.createdAt,
                "updatedAt" to eventDto.updatedAt
            )
            
            Log.d("EventRemoteDataSource", "=== SAVING EVENT TO FIRESTORE ===")
            Log.d("EventRemoteDataSource", "Path: users/$userId/events/${eventDto.id}")
            Log.d("EventRemoteDataSource", "Data: $data")
            
            firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventDto.id)
                .set(data)
                .await()
                
            Log.d("EventRemoteDataSource", "Event saved successfully")
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error saving event: ${e.message}", e)
            throw e
        }
    }

    suspend fun getEvents(userId: String): List<EventDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("events")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                EventDto(
                    id = data["id"] as? String ?: "",
                    academicPeriodId = data["academicPeriodId"] as? String,
                    subjectId = data["subjectId"] as? String,
                    locationId = data["locationId"] as? String,
                    recurrenceRuleId = data["recurrenceRuleId"] as? String,
                    title = data["title"] as? String ?: "",
                    startAt = data["startAt"] as? String ?: "",
                    endAt = data["endAt"] as? String ?: "",
                    locationType = data["locationType"] as? String ?: "NONE",
                    eventType = data["eventType"] as? String ?: "OTHER",
                    meetingUrl = data["meetingUrl"] as? String,
                    notes = data["notes"] as? String,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error getting events: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteEvent(userId: String, eventId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error deleting event: ${e.message}", e)
            throw e
        }
    }

    suspend fun getRecurrenceRules(userId: String): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("recurrenceRules")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error getting recurrence rules: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getRecurrenceDays(userId: String, ruleId: String): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("recurrenceRules")
                .document(ruleId)
                .collection("days")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error getting recurrence days: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun saveRecurrenceRule(userId: String, ruleId: String, data: Map<String, Any>) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("recurrenceRules")
                .document(ruleId)
                .set(data)
                .await()
            Log.d("EventRemoteDataSource", "RecurrenceRule saved to Firestore successfully")
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error saving recurrence rule to Firestore: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveRecurrenceDay(userId: String, ruleId: String, dayId: String, data: Map<String, Any>) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("recurrenceRules")
                .document(ruleId)
                .collection("days")
                .document(dayId)
                .set(data)
                .await()
            Log.d("EventRemoteDataSource", "RecurrenceDay saved to Firestore successfully")
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error saving recurrence day to Firestore: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveReminder(userId: String, eventId: String, reminderId: String, data: Map<String, Any>) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .collection("reminders")
                .document(reminderId)
                .set(data)
                .await()
            Log.d("EventRemoteDataSource", "Reminder saved to Firestore successfully")
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error saving reminder to Firestore: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteRemindersForEvent(userId: String, eventId: String) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .collection("reminders")
                .get()
                .await()
            
            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }
            Log.d("EventRemoteDataSource", "All reminders deleted for event $eventId")
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error deleting reminders from Firestore: ${e.message}", e)
        }
    }

    suspend fun getRemindersForEvent(userId: String, eventId: String): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .collection("reminders")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error getting reminders: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun saveEventTag(userId: String, eventId: String, tagId: String) {
        try {
            val data = mapOf(
                "eventId" to eventId,
                "tagId" to tagId
            )
            firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .collection("eventTags")
                .document(tagId)
                .set(data)
                .await()
            Log.d("EventRemoteDataSource", "EventTag saved successfully")
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error saving event tag: ${e.message}", e)
            throw e
        }
    }

    suspend fun getEventTags(userId: String, eventId: String): List<String> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .collection("eventTags")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.data?.get("tagId") as? String
            }
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error getting event tags: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun deleteEventTag(userId: String, eventId: String, tagId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("events")
                .document(eventId)
                .collection("eventTags")
                .document(tagId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("EventRemoteDataSource", "Error deleting event tag: ${e.message}", e)
            throw e
        }
    }
}
