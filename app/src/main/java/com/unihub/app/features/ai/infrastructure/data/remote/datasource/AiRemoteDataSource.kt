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
    suspend fun sendMessage(message: String): Result<String> {
        return try {
            val response = httpClient.post("$BASE_URL/api/v1/ai/chat") {
                contentType(ContentType.Application.Json)
                setBody(AiChatRequestDto(message = message))
            }

            if (response.status.isSuccess()) {
                val body = response.body<AiChatResponseDto>()
                Result.success(body.response)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error del servidor: ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val BASE_URL = "http://10.0.2.2:8080"
    }
}
