package com.unihub.app.features.settings.infrastructure.data.mapper

import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import com.unihub.app.features.settings.infrastructure.data.remote.dto.PreferencesDto

fun UserPreferences.toDto(): PreferencesDto {
    return PreferencesDto(
        userId = userId,
        themeMode = themeMode.name
    )
}

fun PreferencesDto.toDomain(): UserPreferences {
    return UserPreferences(
        userId = userId,
        themeMode = try {
            ThemeMode.valueOf(themeMode)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    )
}
