package com.unihub.app.features.settings.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserPreferences")
data class UserPreferencesEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "theme_mode")
    val themeMode: String = "SYSTEM"
)
