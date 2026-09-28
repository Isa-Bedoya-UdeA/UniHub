package com.unihub.app.features.ai.presentation.viewmodel

import com.unihub.app.features.ai.application.usecase.SendAiMessageUseCase
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.domain.repository.AiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiChatViewModelTest {

    private lateinit var viewModel: AiChatViewModel
    private lateinit var fakeRepository: TestAiRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = TestAiRepository()
        val useCase = SendAiMessageUseCase(fakeRepository)
        viewModel = AiChatViewModel(useCase)
    }

    @Test
    fun `initial state has empty messages and no loading`() {
        val state = viewModel.state.value
        assertTrue(state.messages.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("", state.inputText)
    }

    @Test
    fun `onInputChanged updates input text`() {
        viewModel.onInputChanged("Hola")
        assertEquals("Hola", viewModel.state.value.inputText)
    }

    @Test
    fun `sendMessage with blank input does nothing`() {
        viewModel.onInputChanged("")
        viewModel.sendMessage()
        assertTrue(viewModel.state.value.messages.isEmpty())
    }

    @Test
    fun `sendMessage adds user message and gets response`() = runTest {
        fakeRepository.response = "Hola, soy UniHub"
        viewModel.onInputChanged("Hola")
        viewModel.sendMessage()

        val state = viewModel.state.value
        assertEquals(2, state.messages.size)
        assertEquals("Hola", state.messages[0].content)
        assertEquals(AiMessageRole.USER, state.messages[0].role)
        assertEquals("Hola, soy UniHub", state.messages[1].content)
        assertEquals(AiMessageRole.ASSISTANT, state.messages[1].role)
        assertFalse(state.isLoading)
        assertEquals("", state.inputText)
    }

    @Test
    fun `sendMessage on failure sets error message`() = runTest {
        fakeRepository.shouldFail = true
        viewModel.onInputChanged("Hola")
        viewModel.sendMessage()

        val state = viewModel.state.value
        assertEquals(1, state.messages.size)
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `clearError clears error message`() = runTest {
        fakeRepository.shouldFail = true
        viewModel.onInputChanged("Hola")
        viewModel.sendMessage()

        assertNotNull(viewModel.state.value.errorMessage)
        viewModel.clearError()
        assertNull(viewModel.state.value.errorMessage)
    }
}

class TestAiRepository : AiRepository {
    var response: String = "Test response"
    var shouldFail: Boolean = false

    override suspend fun sendMessage(message: String): Result<String> {
        return if (shouldFail) {
            Result.failure(Exception("Test error"))
        } else {
            Result.success(response)
        }
    }
}
