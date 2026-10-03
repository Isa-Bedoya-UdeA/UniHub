package com.unihub.app.features.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AiMessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
    ERROR
}

@Serializable
data class AiMessage(
    val id: String,
    val role: AiMessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: AiResponseSource? = null
)
