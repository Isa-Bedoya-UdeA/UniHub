package com.unihub.server

import com.unihub.server.provider.*
import com.unihub.server.retry.RetryPolicy
import com.unihub.server.service.AiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AiServiceTest {

    @Test
    fun `AiService can be instantiated`() {
        val service = AiService()
        assertNotNull(service)
    }

    @Test
    fun `ProviderFallbackChain can be created with default providers`() {
        val chain = ProviderFallbackChain.createDefault()
        assertNotNull(chain)
    }

    @Test
    fun `ProviderFallbackChain fails when no providers are configured`() = runBlocking {
        // Create a chain with unconfigured providers
        val chain = ProviderFallbackChain(
            providers = listOf(
                object : AiProvider {
                    override val name = "TestProvider"
                    override fun isConfigured() = false
                    override suspend fun generateResponse(prompt: String, systemPrompt: String?): Result<AiProviderResponse> =
                        Result.failure(AiProviderError.ConfigurationError("Not configured"))
                }
            )
        )
        
        val result = chain.generateResponse("test")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AiProviderError.ConfigurationError)
    }

    @Test
    fun `ProviderFallbackChain tries providers in order`() = runBlocking {
        val triedProviders = mutableListOf<String>()
        
        val chain = ProviderFallbackChain(
            providers = listOf(
                createMockProvider("Provider1", shouldFail = true, triedList = triedProviders),
                createMockProvider("Provider2", shouldFail = true, triedList = triedProviders),
                createMockProvider("Provider3", shouldFail = false, triedList = triedProviders)
            )
        )
        
        val result = chain.generateResponse("test")
        
        assertTrue(result.isSuccess)
        assertEquals(listOf("Provider1", "Provider2", "Provider3"), triedProviders)
    }

    @Test
    fun `ProviderFallbackChain stops on first success`() = runBlocking {
        val triedProviders = mutableListOf<String>()
        
        val chain = ProviderFallbackChain(
            providers = listOf(
                createMockProvider("Provider1", shouldFail = false, triedList = triedProviders),
                createMockProvider("Provider2", shouldFail = false, triedList = triedProviders),
                createMockProvider("Provider3", shouldFail = false, triedList = triedProviders)
            )
        )
        
        val result = chain.generateResponse("test")
        
        assertTrue(result.isSuccess)
        assertEquals(listOf("Provider1"), triedProviders)
    }

    @Test
    fun `ProviderFallbackChain fails when all providers fail`() = runBlocking {
        val triedProviders = mutableListOf<String>()
        
        val chain = ProviderFallbackChain(
            providers = listOf(
                createMockProvider("Provider1", shouldFail = true, triedList = triedProviders),
                createMockProvider("Provider2", shouldFail = true, triedList = triedProviders),
                createMockProvider("Provider3", shouldFail = true, triedList = triedProviders)
            )
        )
        
        val result = chain.generateResponse("test")
        
        assertTrue(result.isFailure)
        assertEquals(listOf("Provider1", "Provider2", "Provider3"), triedProviders)
    }

    @Test
    fun `RetryPolicy does not retry non-retryable errors`() = runBlocking {
        var attempts = 0
        
        val result = RetryPolicy.executeWithRetry<String>("TestProvider") { attempt ->
            attempts = attempt
            Result.failure(AiProviderError.AuthenticationError("Auth failed"))
        }
        
        assertTrue(result.isFailure)
        assertEquals(1, attempts) // Should only try once, no retries
    }

    @Test
    fun `OpenRouterAiProvider is not configured without API key`() {
        val provider = OpenRouterAiProvider(apiKey = "")
        assertFalse(provider.isConfigured())
    }

    @Test
    fun `GroqAiProvider is not configured without API key`() {
        val provider = GroqAiProvider(apiKey = "")
        assertFalse(provider.isConfigured())
    }

    @Test
    fun `AiService maps errors to user-friendly messages`() = runBlocking {
        val service = AiService(
            providerChain = ProviderFallbackChain(
                providers = listOf(
                    createMockProvider("FailingProvider", shouldFail = true, 
                        error = AiProviderError.NetworkError("Connection failed"))
                )
            )
        )
        
        val result = service.sendMessage("test")
        
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue(errorMessage.contains("conexión") || errorMessage.contains("internet"))
    }

    // Helper function to create mock providers for testing
    private fun createMockProvider(
        providerName: String,
        shouldFail: Boolean,
        triedList: MutableList<String>? = null,
        error: AiProviderError = AiProviderError.UnknownError("Mock error")
    ): AiProvider {
        return object : AiProvider {
            override val name: String = providerName
            
            override fun isConfigured(): Boolean = true
            
            override suspend fun generateResponse(prompt: String, systemPrompt: String?): Result<AiProviderResponse> {
                triedList?.add(providerName)
                return if (shouldFail) {
                    Result.failure(error)
                } else {
                    Result.success(
                        AiProviderResponse(
                            text = "Mock response from $providerName",
                            provider = providerName,
                            model = "mock-model"
                        )
                    )
                }
            }
        }
    }
}
