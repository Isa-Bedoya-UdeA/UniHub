package com.unihub.server.service

import com.unihub.server.client.GeminiClient

class AiService(
    private val geminiClient: GeminiClient = GeminiClient()
) {
    suspend fun sendMessage(message: String): Result<String> {
        return geminiClient.generateContent(message)
    }
}
