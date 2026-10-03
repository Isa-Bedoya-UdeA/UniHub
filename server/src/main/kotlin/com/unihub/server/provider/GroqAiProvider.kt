package com.unihub.server.provider

import com.unihub.server.retry.RetryPolicy
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Groq AI Provider - Second fallback provider
 * Uses OpenAI-compatible API at https://api.groq.com/openai/v1
 * Uses model: openai/gpt-oss-120b
 */
class GroqAiProvider(
    private val apiKey: String = com.unihub.server.config.EnvConfig.get("GROQ_API_KEY")
) : AiProvider {
    
    override val name: String = "Groq"
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000
        }
    }
    
    override fun isConfigured(): Boolean = apiKey.isNotBlank()
    
    override suspend fun generateResponse(
        prompt: String,
        systemPrompt: String?
    ): Result<AiProviderResponse> {
        if (!isConfigured()) {
            return Result.failure(
                AiProviderError.ConfigurationError("GROQ_API_KEY not configured")
            )
        }
        
        return RetryPolicy.executeWithRetry(name) { attempt ->
            try {
                val request = GroqRequest(
                    model = MODEL,
                    messages = buildList {
                        systemPrompt?.let {
                            add(GroqMessage(role = "system", content = it))
                        }
                        add(GroqMessage(role = "user", content = prompt))
                    }
                )
                
                val response = httpClient.post("$API_URL/chat/completions") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $apiKey")
                    setBody(request)
                }
                
                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()
                    val groqResponse = json.decodeFromString<GroqResponse>(responseBody)
                    
                    val text = groqResponse.choices
                        .firstOrNull()
                        ?.message
                        ?.content
                    
                    if (text != null) {
                        Result.success(
                            AiProviderResponse(
                                text = text,
                                provider = name,
                                model = groqResponse.model,
                                tokensUsed = groqResponse.usage?.totalTokens
                            )
                        )
                    } else {
                        Result.failure(
                            AiProviderError.UnknownError("Empty response from Groq")
                        )
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    val retryAfter = RetryPolicy.extractRetryAfter(response)
                    
                    when (response.status.value) {
                        401, 403 -> Result.failure(
                            AiProviderError.AuthenticationError("Groq authentication failed")
                        )
                        429 -> Result.failure(
                            AiProviderError.RateLimited(retryAfter)
                        )
                        in 500..599 -> Result.failure(
                            AiProviderError.ApiError(response.status.value, errorBody)
                        )
                        else -> Result.failure(
                            AiProviderError.ApiError(response.status.value, errorBody)
                        )
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is java.net.SocketTimeoutException -> Result.failure(
                        AiProviderError.TimeoutError(e.message ?: "Connection timeout")
                    )
                    is java.net.UnknownHostException -> Result.failure(
                        AiProviderError.NetworkError("DNS resolution failed")
                    )
                    else -> Result.failure(
                        AiProviderError.NetworkError(e.message ?: "Network error")
                    )
                }
            }
        }
    }
    
    companion object {
        private const val API_URL = "https://api.groq.com/openai/v1"
        private const val MODEL = "llama-3.3-70b-versatile"
    }
}

@Serializable
private data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)

@Serializable
private data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
private data class GroqResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<GroqChoice> = emptyList(),
    val usage: GroqUsage? = null
)

@Serializable
private data class GroqChoice(
    val index: Int = 0,
    val message: GroqMessage? = null,
    val finish_reason: String? = null
)

@Serializable
private data class GroqUsage(
    val prompt_tokens: Int = 0,
    val completion_tokens: Int = 0,
    val total_tokens: Int = 0
) {
    val totalTokens: Int get() = total_tokens
}
