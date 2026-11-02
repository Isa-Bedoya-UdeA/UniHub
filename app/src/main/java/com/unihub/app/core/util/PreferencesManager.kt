package com.unihub.app.core.util

import android.content.Context
import android.content.SharedPreferences
import com.unihub.app.features.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PreferencesManager {
    private const val PREF_NAME = "unihub_preferences"
    private const val KEY_PERMISSION_GUIDE_SHOWN = "permission_guide_shown"
    private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"
    private const val KEY_THEME_MODE = "theme_mode"

    private val _themeModeFlow = MutableStateFlow<ThemeMode?>(null)
    val themeModeFlow: StateFlow<ThemeMode?> = _themeModeFlow.asStateFlow()

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isPermissionGuideShown(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_PERMISSION_GUIDE_SHOWN, false)
    }

    fun setPermissionGuideShown(context: Context, shown: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_PERMISSION_GUIDE_SHOWN, shown).apply()
    }

    fun isOnboardingShown(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_ONBOARDING_SHOWN, false)
    }

    fun setOnboardingShown(context: Context, shown: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_ONBOARDING_SHOWN, shown).apply()
    }

    fun getThemeMode(context: Context): String? {
        val theme = getPreferences(context).getString(KEY_THEME_MODE, null)
        if (_themeModeFlow.value == null && theme != null) {
            _themeModeFlow.value = runCatching { ThemeMode.valueOf(theme) }.getOrNull()
        }
        return theme
    }

    fun setThemeMode(context: Context, themeMode: String) {
        getPreferences(context).edit().putString(KEY_THEME_MODE, themeMode).apply()
        _themeModeFlow.value = runCatching { ThemeMode.valueOf(themeMode) }.getOrNull()
    }

    fun resetAllPreferences(context: Context) {
        getPreferences(context).edit().clear().apply()
        _themeModeFlow.value = ThemeMode.SYSTEM
    }
}
