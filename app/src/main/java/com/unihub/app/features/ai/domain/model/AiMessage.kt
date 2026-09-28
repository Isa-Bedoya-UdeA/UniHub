package com.unihub.app.features.ai.domain.model

enum class AiMessageRole {
    USER, ASSISTANT
}

data class AiMessage(
    val id: String,
    val role: AiMessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
