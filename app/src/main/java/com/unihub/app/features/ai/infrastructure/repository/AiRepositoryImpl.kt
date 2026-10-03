package com.unihub.app.features.ai.infrastructure.repository

import com.unihub.app.features.ai.domain.model.AiResponse
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.ai.infrastructure.data.remote.datasource.AiRemoteDataSource
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource
) : AiRepository {
    override suspend fun sendMessage(message: String): Result<AiResponse> {
        return remoteDataSource.sendMessage(message).map { dto ->
            AiResponse(
                text = dto.response,
                source = AiResponseSource.AI,
                provider = dto.provider,
                model = dto.model
            )
        }
    }
}
