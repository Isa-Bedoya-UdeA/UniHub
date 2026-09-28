package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class AiChatRequest(
    val message: String
)

@Serializable
data class AiChatResponse(
    val response: String
)

@Serializable
data class AiErrorResponse(
    val error: String
)
