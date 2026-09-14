package com.unihub.app.core.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREF_NAME = "unihub_preferences"
    private const val KEY_PERMISSION_GUIDE_SHOWN = "permission_guide_shown"
    private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"

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

    fun resetAllPreferences(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}
