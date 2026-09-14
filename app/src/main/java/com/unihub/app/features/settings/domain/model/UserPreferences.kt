package com.unihub.app.features.settings.domain.model

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

data class UserPreferences(
    val userId: String,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
