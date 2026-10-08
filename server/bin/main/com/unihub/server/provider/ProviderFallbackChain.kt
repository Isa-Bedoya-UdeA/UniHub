package com.unihub.server.provider

/**
 * Provider fallback chain that tries providers in order:
 * 1. OpenRouter (primary)
 * 2. Groq (fallback)
 * 
 * Each provider is tried sequentially. If a provider fails after its retry policy,
 * the next provider is attempted.
 */
class ProviderFallbackChain(
    private val providers: List<AiProvider>
) {
    
    /**
     * Generate a response using the fallback chain.
     * Tries each provider in order until one succeeds.
     * 
     * @param prompt The user's message/prompt
     * @param systemPrompt Optional system prompt for context
     * @return Result containing the response or an error
     */
    suspend fun generateResponse(
        prompt: String,
        systemPrompt: String? = null
    ): Result<AiProviderResponse> {
        val availableProviders = providers.filter { it.isConfigured() }
        
        if (availableProviders.isEmpty()) {
            println("AI configuration error: No providers configured")
            return Result.failure(
                AiProviderError.ConfigurationError(
                    "No AI providers are configured. Please set at least one provider API key."
                )
            )
        }
        
        println("AI fallback chain: ${availableProviders.map { it.name }.joinToString(" -> ")}")
        
        var lastError: Exception? = null
        
        for (provider in availableProviders) {
            println("Trying provider: ${provider.name}")
            
            val result = provider.generateResponse(prompt, systemPrompt)
            
            result.fold(
                onSuccess = { response ->
                    println("Provider success: ${provider.name}")
                    return Result.success(response)
                },
                onFailure = { error ->
                    println("Provider failed: ${provider.name} - ${error.message}")
                    lastError = error as? Exception ?: Exception(error)
                    
                    // Continue to next provider
                }
            )
        }
        
        // All providers failed
        println("All providers failed")
        return Result.failure(
            lastError ?: AiProviderError.UnknownError("All AI providers failed")
        )
    }
    
    companion object {
        /**
         * Create the default provider chain with OpenRouter (primary) and Groq (fallback).
         */
        fun createDefault(): ProviderFallbackChain {
            return ProviderFallbackChain(
                providers = listOf(
                    OpenRouterAiProvider(),
                    GroqAiProvider()
                )
            )
        }
    }
}

