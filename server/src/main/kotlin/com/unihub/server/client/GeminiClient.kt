package com.unihub.server.client

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GeminiClient {

    private val apiKey: String = System.getenv("GEMINI_API_KEY") ?: ""

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun generateContent(prompt: String): Result<String> {
        if (apiKey.isBlank()) {
            return Result.failure(Exception("GEMINI_API_KEY not configured"))
        }

        return try {
            val response = httpClient.post("$GEMINI_API_URL$apiKey:generateContent") {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(
                                        text = SYSTEM_PROMPT + prompt
                                    )
                                )
                            )
                        )
                    )
                )
            }

            if (response.status.isSuccess()) {
                val body = response.body<GeminiResponse>()
                val text = body.candidates
                    .firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (text != null) {
                    Result.success(text)
                } else {
                    Result.failure(Exception("Empty response from Gemini"))
                }
            } else {
                Result.failure(Exception("Gemini API error: ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="

        const val SYSTEM_PROMPT = """Eres el asistente académico de UniHub, una aplicación de organización académica para estudiantes universitarios. 
Responde de forma breve, clara y útil en español. 
Ayuda al estudiante a organizar su día, sus tareas, materias y eventos. 
No inventes datos del usuario. Si no tienes contexto, da consejos generales de organización académica.
Limita tus respuestas a máximo 3 párrafos cortos.

Pregunta del estudiante: """
    }
}

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)
