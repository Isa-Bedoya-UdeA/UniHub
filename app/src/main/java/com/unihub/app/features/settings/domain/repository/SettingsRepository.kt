package com.unihub.app.features.settings.domain.repository

import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getUserPreferences(userId: String): Flow<UserPreferences?>
    suspend fun updateThemeMode(userId: String, themeMode: ThemeMode)
}
