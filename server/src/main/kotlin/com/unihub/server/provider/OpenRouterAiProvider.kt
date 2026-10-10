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
 * OpenRouter AI Provider - First fallback provider
 * Uses OpenAI-compatible API at https://openrouter.ai/api/v1
 * Uses the free router model: openrouter/free
 */
class OpenRouterAiProvider(
    private val apiKey: String = com.unihub.server.config.EnvConfig.get("OPENROUTER_API_KEY")
) : AiProvider {
    
    override val name: String = "OpenRouter"
    
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
                AiProviderError.ConfigurationError("OPENROUTER_API_KEY not configured")
            )
        }
        
        return RetryPolicy.executeWithRetry(name) { attempt ->
            try {
                val request = OpenRouterRequest(
                    model = MODEL,
                    messages = buildList {
                        systemPrompt?.let {
                            add(OpenRouterMessage(role = "system", content = it))
                        }
                        add(OpenRouterMessage(role = "user", content = prompt))
                    }
                )
                
                val response = httpClient.post("$API_URL/chat/completions") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $apiKey")
                    header("HTTP-Referer", "https://unihub.app")
                    header("X-Title", "UniHub")
                    setBody(request)
                }
                
                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()
                    val openRouterResponse = json.decodeFromString<OpenRouterResponse>(responseBody)
                    
                    val text = openRouterResponse.choices
                        .firstOrNull()
                        ?.message
                        ?.content
                    
                    if (text != null) {
                        Result.success(
                            AiProviderResponse(
                                text = text,
                                provider = name,
                                model = openRouterResponse.model,
                                tokensUsed = openRouterResponse.usage?.totalTokens
                            )
                        )
                    } else {
                        Result.failure(
                            AiProviderError.UnknownError("Empty response from OpenRouter")
                        )
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    val retryAfter = RetryPolicy.extractRetryAfter(response)
                    
                    when (response.status.value) {
                        401, 403 -> Result.failure(
                            AiProviderError.AuthenticationError("OpenRouter authentication failed")
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
        private const val API_URL = "https://openrouter.ai/api/v1"
        private const val MODEL = "meta-llama/llama-3.3-70b-instruct:free"
    }
}

@Serializable
private data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>
)

@Serializable
private data class OpenRouterMessage(
    val role: String,
    val content: String
)

@Serializable
private data class OpenRouterResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<OpenRouterChoice> = emptyList(),
    val usage: OpenRouterUsage? = null
)

@Serializable
private data class OpenRouterChoice(
    val index: Int = 0,
    val message: OpenRouterMessage? = null,
    val finish_reason: String? = null
)

@Serializable
private data class OpenRouterUsage(
    val prompt_tokens: Int = 0,
    val completion_tokens: Int = 0,
    val total_tokens: Int = 0
) {
    val totalTokens: Int get() = total_tokens
}
