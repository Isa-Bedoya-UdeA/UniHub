package com.unihub.server.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.unihub.server.models.AiChatRequest
import com.unihub.server.models.AiChatResponse
import com.unihub.server.models.AiErrorResponse
import com.unihub.server.service.AiService

fun Routing.configureAiRoutes() {
    val aiService = AiService()

    route("/api/v1/ai") {
        post("/chat") {
            try {
                val request = call.receive<AiChatRequest>()

                if (request.message.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = "El mensaje no puede estar vacío")
                    )
                    return@post
                }

                if (request.message.length > 2000) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = "El mensaje es demasiado largo (máximo 2000 caracteres)")
                    )
                    return@post
                }

                val response = aiService.sendMessage(request.message)

                response.fold(
                    onSuccess = { reply ->
                        call.respond(
                            HttpStatusCode.OK,
                            AiChatResponse(response = reply)
                        )
                    },
                    onFailure = { error ->
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            AiErrorResponse(error = "Error al procesar la solicitud de IA")
                        )
                    }
                )
            } catch (e: io.ktor.server.request.ReceiveTransformException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AiErrorResponse(error = "Formato de solicitud inválido")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AiErrorResponse(error = "Error interno del servidor")
                )
            }
        }
    }
}
