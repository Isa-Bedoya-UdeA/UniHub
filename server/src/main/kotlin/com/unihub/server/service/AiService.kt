package com.unihub.server.service

import com.unihub.server.provider.AiProviderResponse
import com.unihub.server.provider.ProviderFallbackChain

class AiService(
    private val providerChain: ProviderFallbackChain = ProviderFallbackChain.createDefault()
) {
    /**
     * System prompt for the AI assistant
     */
    private val systemPrompt = """Eres el asistente académico de UniHub, una aplicación de organización académica para estudiantes universitarios. 
Responde de forma breve, clara y útil en español. 
Ayuda al estudiante a organizar su día, sus tareas, materias y eventos. 
No inventes datos del usuario. Si no tienes contexto, da consejos generales de organización académica.
Limita tus respuestas a máximo 3 párrafos cortos."""

    /**
     * Send a message to the AI provider chain.
     * 
     * @param message The user's message
     * @return Result containing the provider response or an error
     */
    suspend fun sendMessage(message: String): Result<AiProviderResponse> {
        val result = providerChain.generateResponse(
            prompt = message,
            systemPrompt = systemPrompt
        )
        
        return result.fold(
            onSuccess = { response ->
                Result.success(response)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
