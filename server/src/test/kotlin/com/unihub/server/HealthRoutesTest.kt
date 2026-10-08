package com.unihub.server

import com.unihub.server.auth.MockFirebaseTokenVerifier
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthRoutesTest {

    @Test
    fun `GET health returns status ok`() = testApplication {
        application {
            configureServer(tokenVerifier = MockFirebaseTokenVerifier())
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\": \"ok\""))
    }

    @Test
    fun `GET api health returns status ok`() = testApplication {
        application {
            configureServer(tokenVerifier = MockFirebaseTokenVerifier())
        }

        val response = client.get("/api/health")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\": \"ok\""))
    }
}
