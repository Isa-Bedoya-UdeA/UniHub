package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class AiConversationMessage(
    val role: String,
    val content: String
)

@Serializable
data class AiChatRequest(
    val message: String,
    val context: String? = null,
    val conversationHistory: List<AiConversationMessage> = emptyList()
)

@Serializable
data class AiChatResponse(
    val response: String,
    val provider: String? = null,
    val model: String? = null
)

@Serializable
data class AiErrorResponse(
    val error: String
)

object AiErrorCodes {
    const val AI_UNAVAILABLE = "AI_UNAVAILABLE"
    const val AI_RATE_LIMITED = "AI_RATE_LIMITED"
    const val AI_INVALID_REQUEST = "AI_INVALID_REQUEST"
    const val AI_CONFIGURATION_ERROR = "AI_CONFIGURATION_ERROR"
    const val AI_NETWORK_ERROR = "AI_NETWORK_ERROR"
    const val AI_UNKNOWN_ERROR = "AI_UNKNOWN_ERROR"
}
