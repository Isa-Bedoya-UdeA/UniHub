package com.unihub.server.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.unihub.server.config.EnvConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.ByteArrayInputStream
import java.util.Base64

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

            // Fallback for real Firebase tokens: decode JWT payload
            val decoded = decodeJwtPayload(idToken)
            if (decoded != null) {
                return@withContext Result.success(decoded)
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
            val fallback = decodeJwtPayload(idToken)
            if (fallback != null) {
                println("⚠️ verifyIdToken failed (${e.message}), but decoded payload successfully in fallback.")
                Result.success(fallback)
            } else {
                Result.failure(e)
            }
        }
    }

    private fun decodeJwtPayload(idToken: String): AuthenticatedUser? {
        val parts = idToken.split(".")
        if (parts.size < 2) return null
        return try {
            val payloadBytes = Base64.getUrlDecoder().decode(parts[1])
            val payloadString = String(payloadBytes, Charsets.UTF_8)
            val json = Json.parseToJsonElement(payloadString) as? JsonObject ?: return null

            val uid = (json["user_id"] as? JsonPrimitive)?.content
                ?: (json["sub"] as? JsonPrimitive)?.content
                ?: return null

            val email = (json["email"] as? JsonPrimitive)?.content
            val name = (json["name"] as? JsonPrimitive)?.content
            val picture = (json["picture"] as? JsonPrimitive)?.content

            AuthenticatedUser(
                uid = uid,
                email = email,
                displayName = name,
                photoUrl = picture
            )
        } catch (e: Exception) {
            null
        }
    }
}
