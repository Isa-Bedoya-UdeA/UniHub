package com.unihub.app.features.tasks.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tag")
data class TagEntity(
    @PrimaryKey
    @ColumnInfo(name = "tag_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String
)

@Entity(
    tableName = "TaskTag",
    primaryKeys = ["task_id", "tag_id"]
)
data class TaskTagEntity(
    @ColumnInfo(name = "task_id")
    val taskId: String,
    
    @ColumnInfo(name = "tag_id")
    val tagId: String
)
