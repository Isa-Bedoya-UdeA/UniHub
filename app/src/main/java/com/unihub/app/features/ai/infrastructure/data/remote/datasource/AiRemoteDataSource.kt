package com.unihub.app.features.ai.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.unihub.app.BuildConfig
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiChatRequestDto
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiChatResponseDto
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiConversationMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AiRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val TAG = "AiRemoteDataSource"
        private val CANDIDATE_URLS = listOf(
            BuildConfig.KTOR_BASE_URL,
            "http://10.0.2.2:8080",
            "http://localhost:8080"
        ).distinct()
        @Volatile
        private var workingBaseUrl: String? = null
    }

    suspend fun sendMessage(
        message: String,
        context: String? = null,
        conversationHistory: List<AiConversationMessageDto> = emptyList()
    ): Result<AiChatResponseDto> {
        val idToken = try {
            firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            Log.w(TAG, "Could not get Firebase ID Token: ${e.message}")
            null
        }

        val candidates = if (workingBaseUrl != null) {
            listOf(workingBaseUrl!!) + (CANDIDATE_URLS - workingBaseUrl!!)
        } else {
            CANDIDATE_URLS
        }

        Log.d(TAG, "Attempting ${candidates.size} candidate URLs")

        var lastException: Exception? = null

        for (baseUrl in candidates) {
            try {
                val url = "$baseUrl/api/v1/ai/chat"

                val response = httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    val token = idToken
                    if (token != null) {
                        header("Authorization", "Bearer $token")
                    }
                    setBody(
                        AiChatRequestDto(
                            message = message,
                            context = context,
                            conversationHistory = conversationHistory
                        )
                    )
                }

                if (response.status.isSuccess()) {
                    workingBaseUrl = baseUrl
                    val body = response.body<AiChatResponseDto>()
                    return Result.success(body)
                } else {
                    val errorBody = response.bodyAsText()
                    Log.e(TAG, "HTTP ${response.status.value} from $baseUrl: $errorBody")
                    workingBaseUrl = baseUrl
                    return Result.failure(Exception("Server error: ${response.status.value}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception with $baseUrl: ${e.javaClass.simpleName}: ${e.message}")
                lastException = e
            }
        }

        Log.e(TAG, "All URLs failed. Last error: ${lastException?.message}")
        return Result.failure(lastException ?: Exception("Could not connect to Ktor server"))
    }
}
