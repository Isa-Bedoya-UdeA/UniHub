package com.unihub.app.features.events.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType

@Entity(tableName = "Event")
data class EventEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "academic_period_id")
    val academicPeriodId: String?,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String?,
    
    @ColumnInfo(name = "location_id")
    val locationId: String?,
    
    @ColumnInfo(name = "recurrence_rule_id")
    val recurrenceRuleId: String?,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "start_at")
    val startAt: String,
    
    @ColumnInfo(name = "end_at")
    val endAt: String,
    
    @ColumnInfo(name = "location_type")
    val locationType: LocationType,
    
    @ColumnInfo(name = "event_type")
    val eventType: EventType,
    
    @ColumnInfo(name = "meeting_url")
    val meetingUrl: String?,
    
    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
