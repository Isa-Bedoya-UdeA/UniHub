package com.unihub.app.features.settings.infrastructure.repository

import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import com.unihub.app.features.settings.domain.repository.SettingsRepository
import com.unihub.app.features.settings.infrastructure.data.local.dao.UserPreferencesDao
import com.unihub.app.features.settings.infrastructure.data.mapper.toDomain
import com.unihub.app.features.settings.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val userPreferencesDao: UserPreferencesDao
) : SettingsRepository {

    override fun getUserPreferences(userId: String): Flow<UserPreferences?> {
        return userPreferencesDao.getUserPreferences(userId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun updateThemeMode(userId: String, themeMode: ThemeMode) {
        val preferences = UserPreferences(userId = userId, themeMode = themeMode)
        userPreferencesDao.insertUserPreferences(preferences.toEntity())
    }
}
