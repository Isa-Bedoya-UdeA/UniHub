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
            println("🤖 AI Route: Received POST /api/v1/ai/chat")
            try {
                val request = call.receive<AiChatRequest>()
                println("🤖 AI Route: Message length: ${request.message.length}, context: ${request.context != null}, history: ${request.conversationHistory.size}")

                if (request.message.isBlank()) {
                    println("🤖 AI Route: Blank message, returning 400")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = com.unihub.server.models.AiErrorCodes.AI_INVALID_REQUEST)
                    )
                    return@post
                }

                if (request.message.length > 2000) {
                    println("🤖 AI Route: Message too long (${request.message.length} chars), returning 400")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = com.unihub.server.models.AiErrorCodes.AI_INVALID_REQUEST)
                    )
                    return@post
                }

                val response = aiService.sendMessage(request.message, request.context, request.conversationHistory)

                response.fold(
                    onSuccess = { reply ->
                        println("🤖 AI Route: Success, responding with ${reply.provider}")
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
                        println("🤖 AI Route: Failed - ${error.javaClass.simpleName}: ${error.message}")
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
                println("🤖 AI Route: BadRequestException - ${e.message}")
                call.respond(
                    HttpStatusCode.BadRequest,
                    AiErrorResponse(error = "AI_INVALID_REQUEST")
                )
            } catch (e: Exception) {
                println("🤖 AI Route: Exception - ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AiErrorResponse(error = "AI_UNKNOWN_ERROR")
                )
            }
        }
    }
}
