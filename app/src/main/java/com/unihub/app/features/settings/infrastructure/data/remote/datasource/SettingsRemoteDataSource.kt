package com.unihub.app.features.settings.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.settings.infrastructure.data.remote.dto.PreferencesDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun savePreferences(userId: String, preferencesDto: PreferencesDto) {
        try {
            val data = mapOf(
                "userId" to preferencesDto.userId,
                "themeMode" to preferencesDto.themeMode
            )
            
            Log.d("SettingsRemoteDataSource", "=== SAVING PREFERENCES TO FIRESTORE ===")
            Log.d("SettingsRemoteDataSource", "Path: users/$userId/preferences/settings")
            
            firestore.collection("users")
                .document(userId)
                .collection("preferences")
                .document("settings")
                .set(data)
                .await()
                
            Log.d("SettingsRemoteDataSource", "Preferences saved successfully")
        } catch (e: Exception) {
            Log.e("SettingsRemoteDataSource", "Error saving preferences: ${e.message}", e)
            throw e
        }
    }

    suspend fun getPreferences(userId: String): PreferencesDto? {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("preferences")
                .document("settings")
                .get()
                .await()
            
            val data = snapshot.data ?: return null
            return PreferencesDto(
                userId = data["userId"] as? String ?: "",
                themeMode = data["themeMode"] as? String ?: "SYSTEM"
            )
        } catch (e: Exception) {
            Log.e("SettingsRemoteDataSource", "Error getting preferences: ${e.message}", e)
            return null
        }
    }
}
