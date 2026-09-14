package com.unihub.app.features.settings.application.usecase

import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(userId: String) = repository.getUserPreferences(userId)
}

class UpdateThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(userId: String, themeMode: ThemeMode) {
        repository.updateThemeMode(userId, themeMode)
    }
}
