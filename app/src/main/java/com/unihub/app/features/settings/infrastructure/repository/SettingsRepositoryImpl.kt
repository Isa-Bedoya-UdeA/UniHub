package com.unihub.app.features.settings.infrastructure.repository

import android.util.Log
import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import com.unihub.app.features.settings.domain.repository.SettingsRepository
import com.unihub.app.features.settings.infrastructure.data.local.datasource.SettingsLocalDataSource
import com.unihub.app.features.settings.infrastructure.data.mapper.toDomain
import com.unihub.app.features.settings.infrastructure.data.mapper.toDto
import com.unihub.app.features.settings.infrastructure.data.mapper.toEntity
import com.unihub.app.features.settings.infrastructure.data.remote.datasource.SettingsRemoteDataSource
import android.content.Context
import com.unihub.app.core.util.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource,
    private val remoteDataSource: SettingsRemoteDataSource,
    @ApplicationContext private val context: Context
) : SettingsRepository {

    override fun getUserPreferences(userId: String): Flow<UserPreferences?> {
        return localDataSource.getUserPreferences(userId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun updateThemeMode(userId: String, themeMode: ThemeMode) {
        val preferences = UserPreferences(userId = userId, themeMode = themeMode)
        localDataSource.insertUserPreferences(preferences.toEntity())
        PreferencesManager.setThemeMode(context, themeMode.name)
        
        try {
            val dto = preferences.toDto()
            remoteDataSource.savePreferences(userId, dto)
        } catch (e: Exception) {
            Log.e("SettingsRepositoryImpl", "Error saving preferences to Firestore: ${e.message}", e)
        }
    }

    override suspend fun syncPreferences(userId: String) {
        try {
            val remotePreferences = remoteDataSource.getPreferences(userId)
            if (remotePreferences != null) {
                localDataSource.insertUserPreferences(remotePreferences.toDomain().toEntity())
                PreferencesManager.setThemeMode(context, remotePreferences.themeMode)
            }
        } catch (e: Exception) {
            Log.e("SettingsRepositoryImpl", "Error syncing preferences from Firestore: ${e.message}")
        }
    }
}
