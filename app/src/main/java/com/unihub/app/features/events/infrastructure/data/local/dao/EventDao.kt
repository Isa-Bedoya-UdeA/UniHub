package com.unihub.app.features.events.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.events.infrastructure.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM Event WHERE user_id = :userId AND start_at >= :start AND end_at <= :end")
    fun getEvents(userId: String, start: String, end: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM Event WHERE event_id = :id")
    suspend fun deleteEvent(id: String)
}
