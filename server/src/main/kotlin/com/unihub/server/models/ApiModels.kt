package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDetails(
    val code: String,
    val message: String
)

@Serializable
data class ApiErrorResponse(
    val error: ApiErrorDetails
) {
    companion object {
        fun create(code: String, message: String): ApiErrorResponse {
            return ApiErrorResponse(ApiErrorDetails(code = code, message = message))
        }
    }
}

object ApiErrorCodes {
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val FORBIDDEN = "FORBIDDEN"
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val NOT_FOUND = "NOT_FOUND"
    const val BAD_REQUEST = "BAD_REQUEST"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val AI_ERROR = "AI_ERROR"
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"
}

@Serializable
data class HealthResponse(
    val status: String,
    val service: String = "unihub-api"
)
