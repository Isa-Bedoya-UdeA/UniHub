package com.unihub.app.features.settings.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PreferencesDto(
    val userId: String = "",
    val themeMode: String = "SYSTEM"
)
