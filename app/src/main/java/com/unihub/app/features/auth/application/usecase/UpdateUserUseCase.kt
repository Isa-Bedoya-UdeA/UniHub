package com.unihub.app.features.auth.application.usecase

import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        repository.updateUser(user)
    }
}
