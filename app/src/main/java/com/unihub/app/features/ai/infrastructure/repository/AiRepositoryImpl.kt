package com.unihub.app.features.ai.infrastructure.repository

import com.unihub.app.features.ai.domain.model.AiResponse
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.model.AiStructuredResponse
import com.unihub.app.features.ai.domain.repository.AiConversationMessage
import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.ai.infrastructure.data.remote.datasource.AiRemoteDataSource
import com.unihub.app.features.ai.infrastructure.data.remote.dto.AiConversationMessageDto
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource
) : AiRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    override suspend fun sendMessage(
        message: String,
        context: String?,
        conversationHistory: List<AiConversationMessage>
    ): Result<AiResponse> {
        val historyDtos = conversationHistory.map {
            AiConversationMessageDto(role = it.role, content = it.content)
        }
        return remoteDataSource.sendMessage(message, context, historyDtos).map { dto ->
            val structured = parseStructuredResponse(dto.response)
            AiResponse(
                text = structured?.message ?: dto.response,
                source = AiResponseSource.AI,
                provider = dto.provider,
                model = dto.model,
                structuredResponse = structured
            )
        }
    }

    private fun parseStructuredResponse(rawText: String): AiStructuredResponse? {
        val cleanText = extractJsonString(rawText) ?: return null
        return try {
            json.decodeFromString<AiStructuredResponse>(cleanText)
        } catch (_: Exception) {
            null
        }
    }

    private fun extractJsonString(text: String): String? {
        val trimmed = text.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed
        }
        val codeBlockRegex = Regex("```(?:json)?\\s*(\\{[\\s\\S]*?\\})\\s*```")
        val match = codeBlockRegex.find(trimmed)
        if (match != null) {
            return match.groupValues[1]
        }
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1)
        }
        return null
    }
}
