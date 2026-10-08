package com.unihub.server.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.unihub.server.config.EnvConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

class FirebaseAdminTokenVerifier : FirebaseTokenVerifier {

    private var initialized = false

    init {
        ensureFirebaseInitialized()
    }

    @Synchronized
    private fun ensureFirebaseInitialized() {
        if (FirebaseApp.getApps().isNotEmpty()) {
            initialized = true
            return
        }

        try {
            val projectId = EnvConfig.get("FIREBASE_PROJECT_ID")
            val clientEmail = EnvConfig.get("FIREBASE_CLIENT_EMAIL")
            val privateKeyRaw = EnvConfig.get("FIREBASE_PRIVATE_KEY")

            if (projectId.isNotBlank() && clientEmail.isNotBlank() && privateKeyRaw.isNotBlank()) {
                val privateKey = privateKeyRaw.replace("\\n", "\n")
                val serviceAccountJson = """
                    {
                      "type": "service_account",
                      "project_id": "$projectId",
                      "client_email": "$clientEmail",
                      "private_key": "$privateKey"
                    }
                """.trimIndent()

                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray())))
                    .build()

                FirebaseApp.initializeApp(options)
                initialized = true
                println("🔒 Firebase Admin SDK initialized successfully with environment credentials.")
            } else {
                println("⚠️ Firebase Admin credentials not fully provided. Token verification will operate in fallback mode.")
            }
        } catch (e: Exception) {
            println("❌ Failed to initialize Firebase Admin SDK: ${e.message}")
        }
    }

    override suspend fun verifyToken(idToken: String): Result<AuthenticatedUser> = withContext(Dispatchers.IO) {
        if (idToken.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("ID token is blank"))
        }

        if (!initialized && FirebaseApp.getApps().isEmpty()) {
            // Development fallback for mock tokens when Firebase Admin is not configured
            if (idToken.startsWith("mock-") || idToken.startsWith("test-")) {
                val uid = idToken.removePrefix("mock-").removePrefix("test-")
                return@withContext Result.success(
                    AuthenticatedUser(
                        uid = uid.ifBlank { "test-user-id" },
                        email = "user@unihub.app",
                        displayName = "UniHub User"
                    )
                )
            }
            return@withContext Result.failure(IllegalStateException("Firebase Admin SDK is not configured"))
        }

        try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
            Result.success(
                AuthenticatedUser(
                    uid = decodedToken.uid,
                    email = decodedToken.email,
                    displayName = decodedToken.name,
                    photoUrl = decodedToken.picture
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
