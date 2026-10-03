package com.unihub.app.features.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AiResponseSource {
    AI,
    LOCAL_FALLBACK,
    LIMITED_MODE
}

data class AiResponse(
    val text: String,
    val source: AiResponseSource = AiResponseSource.AI,
    val provider: String? = null,
    val model: String? = null
)
