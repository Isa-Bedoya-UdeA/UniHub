package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.ai.domain.model.AiResponse
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import javax.inject.Inject

class SendAiMessageUseCase @Inject constructor(
    private val repository: AiRepository,
    private val localAiFallbackUseCase: LocalAiFallbackUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase
) {
    suspend operator fun invoke(message: String): Result<AiResponse> {
        val trimmed = message.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío"))
        }
        if (trimmed.length > MAX_MESSAGE_LENGTH) {
            return Result.failure(IllegalArgumentException("El mensaje es demasiado largo"))
        }

        val userId = getCurrentUidUseCase() ?: "current_user"

        // 1. Check if the input is a deterministic intent (greetings, events, tasks, daily plan, subjects)
        val deterministicResponse = localAiFallbackUseCase.handleDeterministicIntent(trimmed, userId)
        if (deterministicResponse != null) {
            return Result.success(
                AiResponse(
                    text = deterministicResponse,
                    source = AiResponseSource.LOCAL_FALLBACK
                )
            )
        }

        // 2. Call external AI via Ktor
        val remoteResult = repository.sendMessage(trimmed)

        return remoteResult.fold(
            onSuccess = { response -> Result.success(response) },
            onFailure = {
                // 3. Fallback when AI is unavailable/fails: try local keyword response or friendly limited capability message
                val fallbackResponse = localAiFallbackUseCase.handleFallbackForFailedAi(trimmed, userId)
                    ?: "Hay un problema con el asistente. Por ahora puedo ayudarte de forma limitada con tus eventos, tareas y agenda."
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
    }
}
