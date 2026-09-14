package com.unihub.app.features.events.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.unihub.app.features.events.domain.model.ReminderType

@Entity(
    tableName = "EventReminder",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["event_id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("event_id")]
)
data class EventReminderEntity(
    @PrimaryKey
    @ColumnInfo(name = "reminder_id")
    val id: String,

    @ColumnInfo(name = "event_id")
    val eventId: String,

    @ColumnInfo(name = "reminder_type")
    val reminderType: ReminderType,

    @ColumnInfo(name = "value")
    val value: Int,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true
)
