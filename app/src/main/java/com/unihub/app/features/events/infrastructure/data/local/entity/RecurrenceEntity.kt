package com.unihub.app.features.events.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "RecurrenceRule")
data class RecurrenceRuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "recurrence_rule_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "frequency")
    val frequency: String,
    
    @ColumnInfo(name = "interval")
    val interval: Int,
    
    @ColumnInfo(name = "start_date")
    val startDate: String,
    
    @ColumnInfo(name = "end_date")
    val endDate: String,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)

@Entity(
    tableName = "RecurrenceDay",
    primaryKeys = ["recurrence_rule_id", "day_of_week"]
)
data class RecurrenceDayEntity(
    @ColumnInfo(name = "recurrence_rule_id")
    val recurrenceRuleId: String,
    
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int
)
