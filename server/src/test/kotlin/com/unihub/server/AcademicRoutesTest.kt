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

class AcademicRoutesTest {

    private val verifier = MockFirebaseTokenVerifier(
        validTokens = mapOf(
            "test-token" to AuthenticatedUser(uid = "user-academic-1")
        )
    )

    @Test
    fun `GET academic summary returns user summary`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.get("/api/academic/summary") {
            header(HttpHeaders.Authorization, "Bearer test-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("user-academic-1"))
        assertTrue(body.contains("Ingeniería de Sistemas"))
    }

    @Test
    fun `POST academic calculate computes required grade correctly`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.post("/api/academic/calculate") {
            header(HttpHeaders.Authorization, "Bearer test-token")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "currentGrade": 3.0,
                    "evaluatedWeight": 60.0,
                    "targetGrade": 3.5
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        // (3.5 * 100 - 3.0 * 60) / 40 = (350 - 180) / 40 = 170 / 40 = 4.25
        assertTrue(body.contains("\"requiredGrade\": 4.25"))
        assertTrue(body.contains("\"isAchievable\": true"))
    }

    @Test
    fun `POST academic calculate rejects invalid currentGrade`() = testApplication {
        application {
            configureServer(tokenVerifier = verifier)
        }

        val response = client.post("/api/academic/calculate") {
            header(HttpHeaders.Authorization, "Bearer test-token")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "currentGrade": 10.0,
                    "evaluatedWeight": 60.0,
                    "targetGrade": 3.5
                }
            """.trimIndent())
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("VALIDATION_ERROR"))
    }
}
