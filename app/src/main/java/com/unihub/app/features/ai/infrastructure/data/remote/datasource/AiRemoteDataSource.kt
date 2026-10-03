package com.unihub.app.features.ai.infrastructure.data.remote.datasource

import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiChatRequestDto
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiChatResponseDto
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
    suspend fun sendMessage(message: String): Result<AiChatResponseDto> {
        val candidates = if (workingBaseUrl != null) {
            listOf(workingBaseUrl!!) + (CANDIDATE_URLS - workingBaseUrl!!)
        } else {
            CANDIDATE_URLS
        }

        var lastException: Exception? = null

        for (baseUrl in candidates) {
            try {
                val response = httpClient.post("$baseUrl/api/v1/ai/chat") {
                    contentType(ContentType.Application.Json)
                    setBody(AiChatRequestDto(message = message))
                }

                if (response.status.isSuccess()) {
                    workingBaseUrl = baseUrl
                    val body = response.body<AiChatResponseDto>()
                    return Result.success(body)
                } else {
                    val errorBody = response.bodyAsText()
                    // If the server answered with an HTTP error, the server IS reachable at this URL
                    workingBaseUrl = baseUrl
                    return Result.failure(Exception("Error del servidor: ${response.status.value}"))
                }
            } catch (e: Exception) {
                lastException = e
            }
        }

        return Result.failure(lastException ?: Exception("No se pudo conectar al servidor Ktor"))
    }

    companion object {
        private val CANDIDATE_URLS = listOf(
            "http://192.168.128.6:8080",
            "http://localhost:8080",
            "http://10.0.2.2:8080"
        )
        @Volatile
        private var workingBaseUrl: String? = null
    }
}
