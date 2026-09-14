package com.unihub.app.features.events.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "EventTag",
    primaryKeys = ["event_id", "tag_id"]
)
data class EventTagEntity(
    @ColumnInfo(name = "event_id")
    val eventId: String,
    
    @ColumnInfo(name = "tag_id")
    val tagId: String
)
