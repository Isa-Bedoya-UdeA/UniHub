package com.unihub.app.features.ai.domain.repository

import com.unihub.app.features.ai.domain.model.AiResponse

data class AiConversationMessage(
    val role: String,
    val content: String
)

interface AiRepository {
    suspend fun sendMessage(
        message: String,
        context: String? = null,
        conversationHistory: List<AiConversationMessage> = emptyList()
    ): Result<AiResponse>
}
