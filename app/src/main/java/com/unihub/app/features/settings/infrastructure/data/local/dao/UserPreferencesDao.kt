package com.unihub.app.features.settings.infrastructure.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unihub.app.features.settings.infrastructure.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM UserPreferences WHERE user_id = :userId")
    fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferences(preferences: UserPreferencesEntity)

    @Query("UPDATE UserPreferences SET theme_mode = :themeMode WHERE user_id = :userId")
    suspend fun updateThemeMode(userId: String, themeMode: String)
}
