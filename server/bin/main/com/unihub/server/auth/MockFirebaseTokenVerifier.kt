package com.unihub.server.auth

class MockFirebaseTokenVerifier(
    private val validTokens: Map<String, AuthenticatedUser> = emptyMap(),
    private val acceptMockPrefix: Boolean = true
) : FirebaseTokenVerifier {

    override suspend fun verifyToken(idToken: String): Result<AuthenticatedUser> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Token cannot be blank"))
        }

        validTokens[idToken]?.let { user ->
            return Result.success(user)
        }

        if (acceptMockPrefix && (idToken.startsWith("mock-") || idToken.startsWith("test-"))) {
            val uid = idToken.removePrefix("mock-").removePrefix("test-")
            val cleanUid = uid.ifBlank { "test-user-id" }
            return Result.success(
                AuthenticatedUser(
                    uid = cleanUid,
                    email = "$cleanUid@unihub.app",
                    displayName = "Test User $cleanUid"
                )
            )
        }

        return Result.failure(SecurityException("Invalid or expired Firebase ID token"))
    }
}
