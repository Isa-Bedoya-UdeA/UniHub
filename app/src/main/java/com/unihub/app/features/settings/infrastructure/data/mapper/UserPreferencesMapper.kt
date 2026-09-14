package com.unihub.app.features.settings.infrastructure.data.mapper

import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import com.unihub.app.features.settings.infrastructure.data.local.entity.UserPreferencesEntity

fun UserPreferencesEntity.toDomain(): UserPreferences {
    return UserPreferences(
        userId = userId,
        themeMode = try {
            ThemeMode.valueOf(themeMode)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    )
}

fun UserPreferences.toEntity(): UserPreferencesEntity {
    return UserPreferencesEntity(
        userId = userId,
        themeMode = themeMode.name
    )
}
