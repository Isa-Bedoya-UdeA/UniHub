package com.unihub.server.retry

import com.unihub.server.provider.AiProviderError
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.random.Random

/**
 * Centralized retry policy for AI providers.
 * Implements exponential backoff with jitter.
 */
object RetryPolicy {
    
    private const val INITIAL_DELAY_MS = 3000L
    private const val MAX_DELAY_MS = 10000L
    private const val MAX_ATTEMPTS = 2
    private const val JITTER_RANGE_MS = 500L
    
    /**
     * Execute a suspending function with retry logic.
     * 
     * @param providerName Name of the provider for logging
     * @param block The suspending function to execute
     * @return Result of the operation
     */
    suspend fun <T> executeWithRetry(
        providerName: String,
        block: suspend (attempt: Int) -> Result<T>
    ): Result<T> {
        var lastException: Exception? = null
        
        for (attempt in 1..MAX_ATTEMPTS) {
            println("AI provider attempt: $providerName (attempt $attempt/$MAX_ATTEMPTS)")
            
            val result = block(attempt)
            
            if (result.isSuccess) {
                println("AI provider success: $providerName")
                return result
            }
            
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            lastException = error as? Exception ?: Exception(error)
            
            // Check if error is retryable
            if (!isRetryable(error)) {
                println("AI provider non-retryable error: $providerName - ${error.message}")
                return Result.failure(error)
            }
            
            // If this is the last attempt, don't wait
            if (attempt == MAX_ATTEMPTS) {
                println("AI provider failed after $MAX_ATTEMPTS attempts: $providerName")
                break
            }
            
            // Calculate delay with exponential backoff and jitter
            val delayMs = calculateDelay(attempt, error)
            println("AI provider retrying after ${delayMs}ms: $providerName")
            delay(delayMs)
        }
        
        return Result.failure(lastException ?: Exception("Unknown error"))
    }
    
    /**
     * Determine if an error is retryable.
     */
    private fun isRetryable(error: Throwable): Boolean {
        return when (error) {
            is AiProviderError.RateLimited -> true
            is AiProviderError.NetworkError -> true
            is AiProviderError.TimeoutError -> true
            is AiProviderError.ApiError -> {
                // Retry on 5xx errors
                error.statusCode in 500..599
            }
            is AiProviderError.AuthenticationError -> false
            is AiProviderError.ConfigurationError -> false
            else -> false
        }
    }
    
    /**
     * Calculate delay with exponential backoff and jitter.
     * Respects Retry-After header if present.
     */
    private fun calculateDelay(attempt: Int, error: Throwable): Long {
        // Check for Retry-After in rate limit errors
        if (error is AiProviderError.RateLimited && error.retryAfterSeconds != null) {
            val retryAfterMs = error.retryAfterSeconds * 1000
            return min(retryAfterMs, MAX_DELAY_MS)
        }
        
        // Exponential backoff: 3s, 6s, 12s... capped at MAX_DELAY_MS
        val baseDelay = INITIAL_DELAY_MS * (1L shl (attempt - 1))
        val cappedDelay = min(baseDelay, MAX_DELAY_MS)
        
        // Add jitter to avoid thundering herd
        val jitter = Random.nextLong(0, JITTER_RANGE_MS)
        
        return cappedDelay + jitter
    }
    
    /**
     * Extract Retry-After header from HTTP response if present.
     */
    fun extractRetryAfter(response: HttpResponse): Long? {
        return try {
            response.headers["Retry-After"]?.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
