package com.unihub.app.features.ai.domain.repository

interface AiRepository {
    suspend fun sendMessage(message: String): Result<String>
}
