package com.unihub.app.features.ai.infrastructure.data.remote.datasource

import android.util.Log
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiChatRequestDto
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiChatResponseDto
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiConversationMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

class AiRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    companion object {
        private const val TAG = "AiRemoteDataSource"
        private val CANDIDATE_URLS = listOf(
            "http://192.168.128.8:8080",
            "http://localhost:8080",
            "http://10.0.2.2:8080"
        )
        @Volatile
        private var workingBaseUrl: String? = null
    }

    suspend fun sendMessage(
        message: String,
        context: String? = null,
        conversationHistory: List<AiConversationMessageDto> = emptyList()
    ): Result<AiChatResponseDto> {
        val candidates = if (workingBaseUrl != null) {
            listOf(workingBaseUrl!!) + (CANDIDATE_URLS - workingBaseUrl!!)
        } else {
            CANDIDATE_URLS
        }

        Log.d(TAG, "Intentando enviar mensaje a ${candidates.size} URLs candidatas")
        Log.d(TAG, "URLs: ${candidates.joinToString(", ")}")

        var lastException: Exception? = null

        for (baseUrl in candidates) {
            try {
                Log.d(TAG, "Intentando conectar con: $baseUrl")
                val url = "$baseUrl/api/v1/ai/chat"
                Log.d(TAG, "URL completa: $url")
                
                val response = httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        AiChatRequestDto(
                            message = message,
                            context = context,
                            conversationHistory = conversationHistory
                        )
                    )
                }

                Log.d(TAG, "Respuesta recibida - Status: ${response.status.value}")

                if (response.status.isSuccess()) {
                    workingBaseUrl = baseUrl
                    Log.d(TAG, "Éxito con $baseUrl")
                    val body = response.body<AiChatResponseDto>()
                    Log.d(TAG, "Respuesta del servidor: ${body.response.take(100)}...")
                    return Result.success(body)
                } else {
                    val errorBody = response.bodyAsText()
                    Log.e(TAG, "Error HTTP ${response.status.value} de $baseUrl: $errorBody")
                    workingBaseUrl = baseUrl
                    return Result.failure(Exception("Error del servidor: ${response.status.value}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al conectar con $baseUrl: ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                lastException = e
            }
        }

        Log.e(TAG, "Todas las URLs fallaron. Último error: ${lastException?.message}")
        return Result.failure(lastException ?: Exception("No se pudo conectar al servidor Ktor"))
    }
}
