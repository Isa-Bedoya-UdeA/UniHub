package com.unihub.server

import com.unihub.server.auth.AuthenticatedUser
import com.unihub.server.auth.MockFirebaseTokenVerifier
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthMiddlewareTest {

    private val verifier = MockFirebaseTokenVerifier(
        validTokens = mapOf(
            "valid-token-123" to AuthenticatedUser(uid = "user-123", email = "test@unihub.app")
        ),
        acceptMockPrefix = false
    )

    @Test
    fun `Protected route returns 401 when Authorization header is missing`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/profile")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("UNAUTHORIZED"))
    }

    @Test
    fun `Protected route returns 401 when Authorization header is invalid`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "Bearer invalid-token-xyz")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("UNAUTHORIZED"))
    }

    @Test
    fun `Protected route succeeds when token is valid`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "Bearer valid-token-123")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("user-123"))
    }

    @Test
    fun `Protected route returns 401 when token is blank`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "Bearer ")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `Protected route returns 401 when Authorization header has wrong scheme`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "Basic valid-token-123")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `Protected route returns 401 when Authorization header is malformed`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "valid-token-123")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `Health endpoint is public and does not require authentication`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/health")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("ok"))
    }

    @Test
    fun `User can access their own data but not other users data`() = testApplication {
        val multiUserVerifier = MockFirebaseTokenVerifier(
            validTokens = mapOf(
                "token-user-a" to AuthenticatedUser(uid = "user-a", email = "a@unihub.app"),
                "token-user-b" to AuthenticatedUser(uid = "user-b", email = "b@unihub.app")
            ),
            acceptMockPrefix = false
        )

        application {
            configureServer(tokenVerifier = multiUserVerifier)
        }

        // User A accessing their own profile
        val responseA = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "Bearer token-user-a")
        }
        assertEquals(HttpStatusCode.OK, responseA.status)
        assertTrue(responseA.bodyAsText().contains("user-a"))

        // User B accessing their own profile
        val responseB = client.get("/api/profile") {
            header(HttpHeaders.Authorization, "Bearer token-user-b")
        }
        assertEquals(HttpStatusCode.OK, responseB.status)
        assertTrue(responseB.bodyAsText().contains("user-b"))

        // Verify users are isolated (User A response should not contain User B data)
        assertTrue(!responseA.bodyAsText().contains("user-b"))
        assertTrue(!responseB.bodyAsText().contains("user-a"))
    }
}
