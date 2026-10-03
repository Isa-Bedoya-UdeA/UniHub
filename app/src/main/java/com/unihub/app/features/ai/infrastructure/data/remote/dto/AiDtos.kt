package com.unihub.app.features.ai.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiChatRequestDto(
    val message: String
)

@Serializable
data class AiChatResponseDto(
    val response: String,
    val provider: String? = null,
    val model: String? = null
)

@Serializable
data class AiErrorDto(
    val error: String? = null,
    val message: String? = null
)
