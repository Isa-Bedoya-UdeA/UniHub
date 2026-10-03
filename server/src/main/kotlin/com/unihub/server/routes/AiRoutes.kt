package com.unihub.server.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.unihub.server.models.AiChatRequest
import com.unihub.server.models.AiChatResponse
import com.unihub.server.models.AiErrorResponse
import com.unihub.server.service.AiService
import com.unihub.server.provider.AiProviderError

fun Routing.configureAiRoutes() {
    val aiService = AiService()

    route("/api/v1/ai") {
        post("/chat") {
            try {
                val request = call.receive<AiChatRequest>()

                if (request.message.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = com.unihub.server.models.AiErrorCodes.AI_INVALID_REQUEST)
                    )
                    return@post
                }

                if (request.message.length > 2000) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = com.unihub.server.models.AiErrorCodes.AI_INVALID_REQUEST)
                    )
                    return@post
                }

                val response = aiService.sendMessage(request.message)

                response.fold(
                    onSuccess = { reply ->
                        call.respond(
                            HttpStatusCode.OK,
                            AiChatResponse(
                                response = reply.text,
                                provider = reply.provider,
                                model = reply.model
                            )
                        )
                    },
                    onFailure = { error ->
                        val (statusCode, errorCode) = when (error) {
                            is AiProviderError.ConfigurationError -> 
                                HttpStatusCode.ServiceUnavailable to com.unihub.server.models.AiErrorCodes.AI_CONFIGURATION_ERROR
                            is AiProviderError.RateLimited -> 
                                HttpStatusCode.TooManyRequests to com.unihub.server.models.AiErrorCodes.AI_RATE_LIMITED
                            is AiProviderError.AuthenticationError -> 
                                HttpStatusCode.InternalServerError to com.unihub.server.models.AiErrorCodes.AI_CONFIGURATION_ERROR
                            is AiProviderError.NetworkError -> 
                                HttpStatusCode.BadGateway to com.unihub.server.models.AiErrorCodes.AI_NETWORK_ERROR
                            is AiProviderError.TimeoutError -> 
                                HttpStatusCode.GatewayTimeout to com.unihub.server.models.AiErrorCodes.AI_NETWORK_ERROR
                            is AiProviderError.ApiError -> 
                                HttpStatusCode.BadGateway to com.unihub.server.models.AiErrorCodes.AI_UNAVAILABLE
                            else -> 
                                HttpStatusCode.InternalServerError to com.unihub.server.models.AiErrorCodes.AI_UNKNOWN_ERROR
                        }
                        
                        call.respond(
                            statusCode,
                            AiErrorResponse(error = errorCode)
                        )
                    }
                )
            } catch (e: io.ktor.server.plugins.BadRequestException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AiErrorResponse(error = "AI_INVALID_REQUEST")
                )
            } catch (e: Exception) {
                // Log error server-side but don't expose details to client
                println("AI route error: ${e.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AiErrorResponse(error = "AI_UNKNOWN_ERROR")
                )
            }
        }
    }
}
