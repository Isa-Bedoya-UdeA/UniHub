package com.unihub.app.features.events.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.events.infrastructure.data.local.entity.EventEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.EventTagEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceDayEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM Event WHERE user_id = :userId")
    fun getEvents(userId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM Event WHERE user_id = :userId")
    fun getAllEvents(userId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM Event WHERE event_id = :id")
    fun getEventById(id: String): Flow<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM Event WHERE event_id = :id")
    suspend fun deleteEvent(id: String)

    // Recurrence Rules
    @Query("SELECT * FROM RecurrenceRule WHERE recurrence_rule_id = :id")
    fun getRecurrenceRuleById(id: String): Flow<RecurrenceRuleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurrenceRule(rule: RecurrenceRuleEntity)

    @Query("DELETE FROM RecurrenceRule WHERE recurrence_rule_id = :id")
    suspend fun deleteRecurrenceRule(id: String)

    // Recurrence Days
    @Query("SELECT * FROM RecurrenceDay WHERE recurrence_rule_id = :ruleId")
    fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurrenceDay(day: RecurrenceDayEntity)

    @Query("DELETE FROM RecurrenceDay WHERE recurrence_rule_id = :ruleId")
    suspend fun deleteRecurrenceDaysByRule(ruleId: String)

    // Event Tags
    @Query("SELECT * FROM EventTag WHERE event_id = :eventId")
    fun getTagsByEvent(eventId: String): Flow<List<EventTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventTag(eventTag: EventTagEntity)

    @Query("DELETE FROM EventTag WHERE event_id = :eventId AND tag_id = :tagId")
    suspend fun deleteEventTag(eventId: String, tagId: String)
}
