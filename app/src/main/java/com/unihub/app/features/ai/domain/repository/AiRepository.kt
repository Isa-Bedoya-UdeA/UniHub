package com.unihub.app.features.ai.domain.repository

import com.unihub.app.features.ai.domain.model.AiResponse

interface AiRepository {
    suspend fun sendMessage(message: String): Result<AiResponse>
}
