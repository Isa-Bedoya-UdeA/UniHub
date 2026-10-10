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

class AiParseRoutesTest {

    private val verifier = MockFirebaseTokenVerifier(
        validTokens = mapOf(
            "ai-test-token" to AuthenticatedUser(uid = "ai-user-1")
        )
    )

    @Test
    fun `POST ai parse detects CREATE_EVENT action`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.post("/api/ai/parse") {
            header(HttpHeaders.Authorization, "Bearer ai-test-token")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "prompt": "Agrega una clase de Computación Móvil el jueves de 6 a 8"
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"action\": \"CREATE_EVENT\""))
    }

    @Test
    fun `POST ai parse rejects blank prompt`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.post("/api/ai/parse") {
            header(HttpHeaders.Authorization, "Bearer ai-test-token")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "prompt": "   "
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("VALIDATION_ERROR"))
    }
}
