package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.ai.domain.model.AiResponse
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiConversationMessage
import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import javax.inject.Inject

class SendAiMessageUseCase @Inject constructor(
    private val repository: AiRepository,
    private val localAiFallbackUseCase: LocalAiFallbackUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val buildAiContextUseCase: BuildAiContextUseCase,
    private val resolveAndExecuteAiActionUseCase: ResolveAndExecuteAiActionUseCase
) {
    suspend operator fun invoke(
        message: String,
        conversationHistory: List<AiConversationMessage> = emptyList()
    ): Result<AiResponse> {
        val trimmed = message.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío"))
        }
        if (trimmed.length > MAX_MESSAGE_LENGTH) {
            return Result.failure(IllegalArgumentException("El mensaje es demasiado largo"))
        }

        val userId = getCurrentUidUseCase() ?: "current_user"

        val deterministicResponse = localAiFallbackUseCase.handleDeterministicIntent(trimmed, userId)
        if (deterministicResponse != null) {
            return Result.success(
                AiResponse(
                    text = deterministicResponse,
                    source = AiResponseSource.LOCAL_FALLBACK
                )
            )
        }

        val userContext = try {
            buildAiContextUseCase()
        } catch (_: Exception) {
            null
        }

        val recentHistory = conversationHistory.takeLast(MAX_CONVERSATION_MESSAGES)

        val remoteResult = repository.sendMessage(trimmed, userContext, recentHistory)

        return remoteResult.fold(
            onSuccess = { response ->
                val rawStructured = response.structuredResponse
                if (rawStructured != null) {
                    val resolvedStructured = resolveAndExecuteAiActionUseCase.resolve(rawStructured)
                    Result.success(
                        response.copy(
                            text = resolvedStructured.message.ifBlank { response.text },
                            structuredResponse = resolvedStructured
                        )
                    )
                } else {
                    Result.success(response)
                }
            },
            onFailure = {
                val fallbackResponse = localAiFallbackUseCase.handleFallbackForFailedAi(trimmed, userId)
                    ?: "El asistente externo no está disponible en este momento. Puedes consultar tus eventos, tareas y plan del día."
                Result.success(
                    AiResponse(
                        text = fallbackResponse,
                        source = AiResponseSource.LIMITED_MODE
                    )
                )
            }
        )
    }

    companion object {
        const val MAX_MESSAGE_LENGTH = 2000
        const val MAX_CONVERSATION_MESSAGES = 6
    }
}
