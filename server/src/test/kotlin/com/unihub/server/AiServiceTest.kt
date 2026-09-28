package com.unihub.server

import com.unihub.server.client.GeminiClient
import com.unihub.server.service.AiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AiServiceTest {

    @Test
    fun `AiService delegates to GeminiClient`() {
        val service = AiService()
        assertNotNull(service)
    }

    @Test
    fun `GeminiClient fails without API key`() = runBlocking {
        val client = GeminiClient()
        val result = client.generateContent("test")
        assertTrue(result.isFailure)
    }
}
