package com.unihub.app.features.settings.infrastructure.data.local.datasource

import com.unihub.app.features.settings.infrastructure.data.local.dao.UserPreferencesDao
import com.unihub.app.features.settings.infrastructure.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSource @Inject constructor(
    private val userPreferencesDao: UserPreferencesDao
) {
    fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> =
        userPreferencesDao.getUserPreferences(userId)

    suspend fun insertUserPreferences(preferences: UserPreferencesEntity) =
        userPreferencesDao.insertUserPreferences(preferences)

    suspend fun updateThemeMode(userId: String, themeMode: String) =
        userPreferencesDao.updateThemeMode(userId, themeMode)
}
