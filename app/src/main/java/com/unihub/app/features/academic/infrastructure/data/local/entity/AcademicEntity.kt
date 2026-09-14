package com.unihub.app.features.academic.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AcademicPeriod")
data class AcademicPeriodEntity(
    @PrimaryKey
    @ColumnInfo(name = "academic_period_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "study_id")
    val studyId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "start_date")
    val startDate: String,
    
    @ColumnInfo(name = "end_date")
    val endDate: String,
    
    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)

@Entity(tableName = "Grade")
data class GradeEntity(
    @PrimaryKey
    @ColumnInfo(name = "grade_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    
    @ColumnInfo(name = "academic_period_id")
    val academicPeriodId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "value")
    val value: Double,
    
    @ColumnInfo(name = "weight")
    val weight: Double,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
