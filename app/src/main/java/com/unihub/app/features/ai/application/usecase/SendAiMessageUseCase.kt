package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.ai.domain.repository.AiRepository
import javax.inject.Inject

class SendAiMessageUseCase @Inject constructor(
    private val repository: AiRepository
) {
    suspend operator fun invoke(message: String): Result<String> {
        if (message.isBlank()) {
            return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío"))
        }
        if (message.length > MAX_MESSAGE_LENGTH) {
            return Result.failure(IllegalArgumentException("El mensaje es demasiado largo"))
        }
        return repository.sendMessage(message)
    }

    companion object {
        const val MAX_MESSAGE_LENGTH = 2000
    }
}
