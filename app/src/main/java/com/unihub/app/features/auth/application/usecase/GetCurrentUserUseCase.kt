package com.unihub.app.features.auth.application.usecase

import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String): Flow<User?> {
        return repository.getUser(userId)
    }
}
