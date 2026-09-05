package com.unihub.app.features.tasks.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus

@Entity(tableName = "Task")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "academic_period_id")
    val academicPeriodId: String?,
    
    @ColumnInfo(name = "subject_id")
    val subjectId: String?,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "due_at")
    val dueAt: String?,
    
    @ColumnInfo(name = "priority")
    val priority: TaskPriority,
    
    @ColumnInfo(name = "status")
    val status: TaskStatus,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
