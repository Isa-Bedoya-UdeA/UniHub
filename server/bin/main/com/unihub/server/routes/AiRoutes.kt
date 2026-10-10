package com.unihub.server.routes

import com.unihub.server.auth.FirebaseTokenVerifier
import com.unihub.server.auth.authenticatedUser
import com.unihub.server.auth.authenticateFirebase
import com.unihub.server.models.*
import com.unihub.server.provider.AiProviderError
import com.unihub.server.service.AiService
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureAiRoutes(
    tokenVerifier: FirebaseTokenVerifier? = null,
    aiService: AiService = AiService()
) {
    fun Route.chatRoute() {
        post("/chat") {
            try {
                val request = call.receive<AiChatRequest>()

                if (request.message.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = AiErrorCodes.AI_INVALID_REQUEST)
                    )
                    return@post
                }

                if (request.message.length > 2000) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = AiErrorCodes.AI_INVALID_REQUEST)
                    )
                    return@post
                }

                val response = aiService.sendMessage(request.message, request.context, request.conversationHistory)

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
                        println("🚨 AI route error: ${error.javaClass.simpleName} - ${error.message}")
                        val (statusCode, errorCode) = when (error) {
                            is AiProviderError.ConfigurationError -> 
                                HttpStatusCode.ServiceUnavailable to AiErrorCodes.AI_CONFIGURATION_ERROR
                            is AiProviderError.RateLimited -> 
                                HttpStatusCode.TooManyRequests to AiErrorCodes.AI_RATE_LIMITED
                            is AiProviderError.AuthenticationError -> 
                                HttpStatusCode.InternalServerError to AiErrorCodes.AI_CONFIGURATION_ERROR
                            is AiProviderError.NetworkError -> 
                                HttpStatusCode.BadGateway to AiErrorCodes.AI_NETWORK_ERROR
                            is AiProviderError.TimeoutError -> 
                                HttpStatusCode.GatewayTimeout to AiErrorCodes.AI_NETWORK_ERROR
                            is AiProviderError.ApiError -> 
                                HttpStatusCode.BadGateway to AiErrorCodes.AI_UNAVAILABLE
                            else -> 
                                HttpStatusCode.InternalServerError to AiErrorCodes.AI_UNKNOWN_ERROR
                        }
                        
                        call.respond(
                            statusCode,
                            AiErrorResponse(error = errorCode)
                        )
                    }
                )
            } catch (e: BadRequestException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AiErrorResponse(error = AiErrorCodes.AI_INVALID_REQUEST)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AiErrorResponse(error = AiErrorCodes.AI_UNKNOWN_ERROR)
                )
            }
        }
    }

    if (tokenVerifier != null) {
        route("/api/v1/ai") {
            authenticateFirebase(tokenVerifier) {
                chatRoute()
            }
        }
    } else {
        route("/api/v1/ai") {
            chatRoute()
        }
    }

    tokenVerifier?.let { verifier ->
        route("/api/ai") {
            authenticateFirebase(verifier) {
                post("/parse") {
                    val user = call.authenticatedUser
                    if (user == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiErrorResponse.create(
                                code = ApiErrorCodes.UNAUTHORIZED,
                                message = "User context not found"
                            )
                        )
                        return@post
                    }

                    val request = try {
                        call.receive<AiParseRequest>()
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiErrorResponse.create(
                                code = ApiErrorCodes.VALIDATION_ERROR,
                                message = "Malformed request payload"
                            )
                        )
                        return@post
                    }

                    if (request.prompt.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiErrorResponse.create(
                                code = ApiErrorCodes.VALIDATION_ERROR,
                                message = "Prompt cannot be blank"
                            )
                        )
                        return@post
                    }

                    if (request.prompt.length > 2000) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiErrorResponse.create(
                                code = ApiErrorCodes.VALIDATION_ERROR,
                                message = "Prompt exceeds maximum allowed length of 2000 characters"
                            )
                        )
                        return@post
                    }

                    val detectedAction = parseActionFromPrompt(request.prompt)
                    val result = aiService.sendMessage(
                        message = "Analiza y genera una respuesta para la instrucción: ${request.prompt}",
                        context = request.context
                    )

                    val replyText = result.getOrNull()?.text ?: "Instrucción procesada correctamente."

                    call.respond(
                        HttpStatusCode.OK,
                        AiParseResponse(
                            action = detectedAction,
                            rawPrompt = request.prompt,
                            structuredData = extractStructuredParams(request.prompt, detectedAction),
                            reply = replyText
                        )
                    )
                }
            }
        }
    }
}

private fun parseActionFromPrompt(prompt: String): String {
    val lower = prompt.lowercase()
    return when {
        lower.contains("clase") || lower.contains("evento") || lower.contains("reunión") || lower.contains("reunion") || lower.contains("agrega") || lower.contains("crea evento") -> "CREATE_EVENT"
        lower.contains("tarea") || lower.contains("entrega") || lower.contains("pendiente") || lower.contains("crea tarea") -> "CREATE_TASK"
        lower.contains("materia") || lower.contains("curso") || lower.contains("asignatura") || lower.contains("crea materia") -> "CREATE_SUBJECT"
        lower.contains("nota") || lower.contains("parcial") || lower.contains("calificación") || lower.contains("calificacion") || lower.contains("registra") -> "REGISTER_GRADE"
        else -> "UNKNOWN"
    }
}

private fun extractStructuredParams(prompt: String, action: String): Map<String, String> {
    val map = mutableMapOf<String, String>()
    map["detected_action"] = action
    map["input_length"] = prompt.length.toString()
    return map
}
