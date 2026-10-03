package com.unihub.server.provider

/**
 * Abstraction for AI providers.
 * Each provider implements this interface to communicate with its specific API.
 */
interface AiProvider {
    /**
     * Provider name for logging and identification
     */
    val name: String
    
    /**
     * Generate a response for the given prompt
     * @param prompt The user's message/prompt
     * @param systemPrompt Optional system prompt for context
     * @return Result containing the response text or an error
     */
    suspend fun generateResponse(
        prompt: String,
        systemPrompt: String? = null
    ): Result<AiProviderResponse>
    
    /**
     * Check if this provider is properly configured (has API key, etc.)
     */
    fun isConfigured(): Boolean
}

/**
 * Response from an AI provider
 */
data class AiProviderResponse(
    val text: String,
    val provider: String,
    val model: String? = null,
    val tokensUsed: Int? = null
)

/**
 * Error types for AI provider failures
 */
sealed class AiProviderError(override val message: String) : Exception(message) {
    data class RateLimited(val retryAfterSeconds: Long? = null) : 
        AiProviderError("El servicio de IA está temporalmente saturado. Por favor, intenta de nuevo en unos momentos.")
    data class AuthenticationError(val details: String) : 
        AiProviderError("Error de autenticación con el servicio de IA: $details")
    data class NetworkError(val details: String) : 
        AiProviderError("Error de conexión con el servicio de IA. Verifica tu conexión a internet: $details")
    data class ApiError(val statusCode: Int, val details: String) : 
        AiProviderError("El servicio de IA reportó un error ($statusCode): $details")
    data class ConfigurationError(val details: String) : 
        AiProviderError("El servicio de IA no está configurado correctamente: $details")
    data class TimeoutError(val details: String) : 
        AiProviderError("El servicio de IA tardó demasiado en responder: $details")
    data class UnknownError(val details: String) : 
        AiProviderError("Ocurrió un error al procesar tu mensaje: $details")
}
