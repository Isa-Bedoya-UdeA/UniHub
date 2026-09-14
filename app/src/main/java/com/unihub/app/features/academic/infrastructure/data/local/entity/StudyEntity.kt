package com.unihub.app.features.academic.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Study")
data class StudyEntity(
    @PrimaryKey
    @ColumnInfo(name = "study_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "institution")
    val institution: String,
    
    @ColumnInfo(name = "total_credits")
    val totalCredits: Int,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
