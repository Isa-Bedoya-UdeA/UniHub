package com.unihub.app.features.subjects.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Subject")
data class SubjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "subject_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "academic_period_id")
    val academicPeriodId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "code")
    val code: String?,
    
    @ColumnInfo(name = "credits")
    val credits: Int?,
    
    @ColumnInfo(name = "professor")
    val professor: String?,
    
    @ColumnInfo(name = "color")
    val color: String?,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
