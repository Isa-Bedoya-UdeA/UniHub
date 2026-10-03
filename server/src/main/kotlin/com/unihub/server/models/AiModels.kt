package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class AiChatRequest(
    val message: String
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

/**
 * Standardized AI error codes returned to the Android client.
 * These are provider-agnostic and do not expose internal details.
 */
object AiErrorCodes {
    const val AI_UNAVAILABLE = "AI_UNAVAILABLE"
    const val AI_RATE_LIMITED = "AI_RATE_LIMITED"
    const val AI_INVALID_REQUEST = "AI_INVALID_REQUEST"
    const val AI_CONFIGURATION_ERROR = "AI_CONFIGURATION_ERROR"
    const val AI_NETWORK_ERROR = "AI_NETWORK_ERROR"
    const val AI_UNKNOWN_ERROR = "AI_UNKNOWN_ERROR"
}
