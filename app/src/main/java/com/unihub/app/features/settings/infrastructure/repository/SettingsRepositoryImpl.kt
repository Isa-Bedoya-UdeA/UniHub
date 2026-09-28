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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource,
    private val remoteDataSource: SettingsRemoteDataSource
) : SettingsRepository {

    override fun getUserPreferences(userId: String): Flow<UserPreferences?> {
        return localDataSource.getUserPreferences(userId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun updateThemeMode(userId: String, themeMode: ThemeMode) {
        Log.d("FIRESTORE_DEBUG", "=== UPDATE THEME MODE CALLED ===")
        Log.d("FIRESTORE_DEBUG", "User ID: $userId")
        Log.d("FIRESTORE_DEBUG", "Theme Mode: $themeMode")
        
        val preferences = UserPreferences(userId = userId, themeMode = themeMode)
        localDataSource.insertUserPreferences(preferences.toEntity())
        Log.d("FIRESTORE_DEBUG", "Preferences saved to Room successfully")
        
        try {
            Log.d("FIRESTORE_DEBUG", "About to call remoteDataSource.savePreferences()")
            val dto = preferences.toDto()
            Log.d("FIRESTORE_DEBUG", "DTO created: $dto")
            remoteDataSource.savePreferences(userId, dto)
            Log.d("FIRESTORE_DEBUG", "remoteDataSource.savePreferences() completed successfully")
        } catch (e: Exception) {
            Log.e("FIRESTORE_DEBUG", "ERROR saving preferences to Firestore: ${e.message}", e)
            Log.e("FIRESTORE_DEBUG", "Exception type: ${e.javaClass.simpleName}")
            Log.e("FIRESTORE_DEBUG", "Stack trace: ${e.stackTraceToString()}")
        }
    }

    override suspend fun syncPreferences(userId: String) {
        try {
            val remotePreferences = remoteDataSource.getPreferences(userId)
            if (remotePreferences != null) {
                localDataSource.insertUserPreferences(remotePreferences.toDomain().toEntity())
            }
        } catch (e: Exception) {
            Log.e("SettingsRepositoryImpl", "Error syncing preferences from Firestore: ${e.message}")
        }
    }
}
