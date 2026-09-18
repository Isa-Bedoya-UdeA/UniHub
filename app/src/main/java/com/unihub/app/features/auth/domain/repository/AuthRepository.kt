package com.unihub.app.features.auth.domain.repository

import com.unihub.app.features.auth.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<AuthState>
    fun getCurrentUid(): String?
    suspend fun signInWithGoogle(idToken: String): Result<String>
    suspend fun signOut()
}
