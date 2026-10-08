package com.unihub.server.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUser(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
)

interface FirebaseTokenVerifier {
    suspend fun verifyToken(idToken: String): Result<AuthenticatedUser>
}
