package com.unihub.app.features.auth.infrastructure.repository

import com.google.firebase.auth.FirebaseAuth
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.auth.infrastructure.data.remote.FirebaseAuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) : AuthRepository {

    override fun observeAuthState(): Flow<AuthState> {
        return authDataSource.observeAuthState().map { isSignedIn ->
            if (isSignedIn) {
                val uid = authDataSource.getCurrentUid()
                if (uid != null) AuthState.Authenticated(uid)
                else AuthState.Unauthenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    override fun getCurrentUid(): String? = authDataSource.getCurrentUid()

    override suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val result = authDataSource.signInWithGoogle(idToken)
            val firebaseUser = result.user ?: return Result.failure(Exception("No se pudo obtener el usuario"))
            val uid = firebaseUser.uid
            
            // Check if user already exists in Room
            val existingUser = userRepository.getUser(uid).first()
            val now = Instant.now().toString()
            
            val user = if (existingUser != null) {
                // User exists - preserve edited data, only update email if needed
                existingUser.copy(
                    email = firebaseUser.email ?: existingUser.email,
                    updatedAt = now
                )
            } else {
                // New user - create with Firebase data
                User(
                    userId = uid,
                    name = firebaseUser.displayName ?: "Usuario",
                    email = firebaseUser.email ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = now,
                    updatedAt = now
                )
            }
            
            userRepository.saveUser(user)
            
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        authDataSource.signOut()
    }
}
