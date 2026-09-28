package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.ai.domain.repository.AiRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SendAiMessageUseCaseTest {

    private lateinit var useCase: SendAiMessageUseCase
    private lateinit var fakeRepository: FakeAiRepository

    @Before
    fun setUp() {
        fakeRepository = FakeAiRepository()
        useCase = SendAiMessageUseCase(fakeRepository)
    }

    @Test
    fun `invoke with valid message returns success`() = runBlocking {
        fakeRepository.response = "Hola, ¿en qué puedo ayudarte?"
        val result = useCase("Hola")
        assertTrue(result.isSuccess)
        assertEquals("Hola, ¿en qué puedo ayudarte?", result.getOrNull())
    }

    @Test
    fun `invoke with blank message returns failure`() = runBlocking {
        val result = useCase("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke with whitespace-only message returns failure`() = runBlocking {
        val result = useCase("   ")
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke with message exceeding max length returns failure`() = runBlocking {
        val longMessage = "a".repeat(SendAiMessageUseCase.MAX_MESSAGE_LENGTH + 1)
        val result = useCase(longMessage)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke with message at max length returns success`() = runBlocking {
        fakeRepository.response = "OK"
        val message = "a".repeat(SendAiMessageUseCase.MAX_MESSAGE_LENGTH)
        val result = useCase(message)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke propagates repository failure`() = runBlocking {
        fakeRepository.shouldFail = true
        val result = useCase("Hola")
        assertTrue(result.isFailure)
    }
}

class FakeAiRepository : AiRepository {
    var response: String = ""
    var shouldFail: Boolean = false

    override suspend fun sendMessage(message: String): Result<String> {
        return if (shouldFail) {
            Result.failure(Exception("Fake error"))
        } else {
            Result.success(response)
        }
    }
}
