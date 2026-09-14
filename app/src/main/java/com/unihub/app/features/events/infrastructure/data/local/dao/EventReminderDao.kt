package com.unihub.app.features.events.infrastructure.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unihub.app.features.events.infrastructure.data.local.entity.EventReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventReminderDao {
    @Query("SELECT * FROM EventReminder WHERE event_id = :eventId")
    fun getRemindersForEvent(eventId: String): Flow<List<EventReminderEntity>>

    @Query("SELECT * FROM EventReminder WHERE reminder_id = :id")
    fun getReminderById(id: String): Flow<EventReminderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: EventReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<EventReminderEntity>)

    @Query("DELETE FROM EventReminder WHERE reminder_id = :id")
    suspend fun deleteReminder(id: String)

    @Query("DELETE FROM EventReminder WHERE event_id = :eventId")
    suspend fun deleteRemindersForEvent(eventId: String)

    @Query("SELECT * FROM EventReminder WHERE is_enabled = 1")
    fun getAllEnabledReminders(): Flow<List<EventReminderEntity>>
}
