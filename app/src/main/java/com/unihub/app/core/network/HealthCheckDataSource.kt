package com.unihub.app.core.network

import android.util.Log
import com.unihub.app.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class HealthResponseDto(
    val status: String = "",
    val service: String = ""
)

@Singleton
class HealthCheckDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun checkHealth(): Result<HealthResponseDto> {
        val baseUrl = BuildConfig.KTOR_BASE_URL
        return try {
            val response = httpClient.get("$baseUrl/api/health")
            if (response.status.isSuccess()) {
                val body = json.decodeFromString<HealthResponseDto>(response.bodyAsText())
                Result.success(body)
            } else {
                Result.failure(Exception("Health check failed: HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Log.e("HealthCheckDataSource", "Health check failed: ${e.message}")
            Result.failure(e)
        }
    }
}
