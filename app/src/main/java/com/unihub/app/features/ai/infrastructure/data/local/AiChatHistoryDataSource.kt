package com.unihub.app.features.ai.infrastructure.data.local

import android.content.Context
import com.unihub.app.features.ai.domain.model.AiMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatHistoryDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun loadMessages(): List<AiMessage> {
        val savedDate = prefs.getString(KEY_SAVED_DATE, null)
        val today = LocalDate.now().toString()

        // If from another day, start fresh
        if (savedDate != today) {
            clearMessages()
            return emptyList()
        }

        val jsonString = prefs.getString(KEY_MESSAGES, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<AiMessage>>(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveMessages(messages: List<AiMessage>) {
        val today = LocalDate.now().toString()
        val jsonString = json.encodeToString(messages)
        prefs.edit()
            .putString(KEY_SAVED_DATE, today)
            .putString(KEY_MESSAGES, jsonString)
            .apply()
    }

    fun clearMessages() {
        prefs.edit()
            .remove(KEY_SAVED_DATE)
            .remove(KEY_MESSAGES)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "unihub_ai_chat_history"
        private const val KEY_SAVED_DATE = "saved_date"
        private const val KEY_MESSAGES = "saved_messages"
    }
}
