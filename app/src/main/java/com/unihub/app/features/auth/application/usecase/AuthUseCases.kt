package com.unihub.app.features.auth.application.usecase

import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthState> = repository.observeAuthState()
}

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<String> = repository.signInWithGoogle(idToken)
}

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.signOut()
}

class GetCurrentUidUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): String? = repository.getCurrentUid()
}
