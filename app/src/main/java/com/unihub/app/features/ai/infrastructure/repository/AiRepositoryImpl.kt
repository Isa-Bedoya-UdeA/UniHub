package com.unihub.app.features.ai.infrastructure.repository

import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.ai.infrastructure.data.remote.datasource.AiRemoteDataSource
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource
) : AiRepository {
    override suspend fun sendMessage(message: String): Result<String> {
        return remoteDataSource.sendMessage(message)
    }
}
