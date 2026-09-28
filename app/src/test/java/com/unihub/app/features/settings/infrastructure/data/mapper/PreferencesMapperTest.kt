package com.unihub.app.features.settings.infrastructure.data.mapper

import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import com.unihub.app.features.settings.infrastructure.data.local.entity.UserPreferencesEntity
import com.unihub.app.features.settings.infrastructure.data.remote.dto.PreferencesDto
import org.junit.Assert.assertEquals
import org.junit.Test

class PreferencesMapperTest {

    @Test
    fun `UserPreferences toEntity and back preserves data`() {
        val prefs = UserPreferences(userId = "u1", themeMode = ThemeMode.DARK)

        val entity = prefs.toEntity()
        val restored = entity.toDomain()

        assertEquals(prefs, restored)
    }

    @Test
    fun `UserPreferences toDto and back preserves data`() {
        val prefs = UserPreferences(userId = "u1", themeMode = ThemeMode.LIGHT)

        val dto = prefs.toDto()
        val restored = dto.toDomain()

        assertEquals(prefs, restored)
    }

    @Test
    fun `UserPreferencesEntity with invalid themeMode defaults to SYSTEM`() {
        val entity = UserPreferencesEntity(userId = "u1", themeMode = "INVALID")

        val prefs = entity.toDomain()

        assertEquals(ThemeMode.SYSTEM, prefs.themeMode)
    }

    @Test
    fun `PreferencesDto with invalid themeMode defaults to SYSTEM`() {
        val dto = PreferencesDto(userId = "u1", themeMode = "INVALID")

        val prefs = dto.toDomain()

        assertEquals(ThemeMode.SYSTEM, prefs.themeMode)
    }

    @Test
    fun `default UserPreferences uses SYSTEM theme`() {
        val prefs = UserPreferences(userId = "u1")

        assertEquals(ThemeMode.SYSTEM, prefs.themeMode)
    }
}
