package com.unihub.app.features.ai.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.ai.application.usecase.LocalAiFallbackUseCase
import com.unihub.app.features.ai.application.usecase.SendAiMessageUseCase
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.domain.model.AiResponse
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.ai.presentation.state.AiChatStatus
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.application.usecase.GetCurrentUserUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.*
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
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

        val fakeEventRepo = object : EventRepository {
            override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> = flowOf(emptyList())
            override fun getAllEvents(userId: String): Flow<List<Event>> = flowOf(emptyList())
            override fun getEventById(id: String): Flow<Event?> = flowOf(null)
            override suspend fun saveEvent(event: Event) {}
            override suspend fun updateEvent(event: Event) {}
            override suspend fun deleteEvent(id: String) {}
            override suspend fun syncEvents(userId: String) {}
            override fun getRemindersForEvent(eventId: String): Flow<List<EventReminder>> = flowOf(emptyList())
            override suspend fun saveReminders(eventId: String, reminders: List<EventReminder>) {}
            override suspend fun deleteRemindersForEvent(eventId: String) {}
            override fun getAllEnabledReminders(): Flow<List<EventReminder>> = flowOf(emptyList())
            override fun getRecurrenceRule(ruleId: String): Flow<RecurrenceRule?> = flowOf(null)
            override fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDay>> = flowOf(emptyList())
            override suspend fun saveRecurrenceRule(rule: RecurrenceRule) {}
            override suspend fun saveRecurrenceDay(day: RecurrenceDay) {}
            override suspend fun deleteRecurrenceRule(id: String) {}
            override suspend fun deleteRecurrenceDaysByRule(ruleId: String) {}
            override fun getEventTags(eventId: String): Flow<List<EventTag>> = flowOf(emptyList())
            override suspend fun saveEventTag(eventTag: EventTag) {}
            override suspend fun deleteEventTag(eventId: String, tagId: String) {}
            override suspend fun deleteEventTagsForEvent(eventId: String) {}
        }

        val fakeTaskRepo = object : TaskRepository {
            override fun getTasks(userId: String): Flow<List<Task>> = flowOf(emptyList())
            override fun getTasksBySubject(subjectId: String): Flow<List<Task>> = flowOf(emptyList())
            override fun getTaskById(id: String): Flow<Task?> = flowOf(null)
            override suspend fun saveTask(task: Task) {}
            override suspend fun updateTask(task: Task) {}
            override suspend fun deleteTask(id: String) {}
            override suspend fun updateTaskStatus(id: String, status: TaskStatus) {}
            override suspend fun syncTasks(userId: String) {}
            override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {}
            override suspend fun getTaskTags(userId: String, taskId: String): List<String> = emptyList()
        }

        val fakeSubjectRepo = object : SubjectRepository {
            override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> = flowOf(emptyList())
            override fun getAllSubjects(userId: String): Flow<List<Subject>> = flowOf(emptyList())
            override fun getSubjectById(id: String): Flow<Subject?> = flowOf(null)
            override suspend fun saveSubject(subject: Subject) {}
            override suspend fun updateSubject(subject: Subject) {}
            override suspend fun deleteSubject(id: String) {}
            override suspend fun syncSubjects(userId: String) {}
        }

        val fakeAuthRepo = object : AuthRepository {
            override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Unauthenticated)
            override fun getCurrentUid(): String = "test_user_id"
            override suspend fun signInWithGoogle(idToken: String): Result<String> = Result.success("ok")
            override suspend fun signOut() {}
            override suspend fun syncExistingUser(userId: String) {}
        }

        val fakeUserRepo = object : UserRepository {
            override fun getUser(userId: String): Flow<User?> = flowOf(
                User(userId, "Juan Pérez", "juan@udea.edu.co", null, "2026-01-01", "2026-01-01")
            )
            override suspend fun saveUser(user: User) {}
            override suspend fun saveUserLocalOnly(user: User) {}
            override suspend fun updateUser(user: User) {}
            override suspend fun deleteUser(user: User) {}
            override suspend fun uploadProfileImage(userId: String, imageUri: android.net.Uri): Result<String> = Result.success("")
            override suspend fun syncProfile(userId: String) {}
        }

        val localFallbackUseCase = LocalAiFallbackUseCase(
            getEventsUseCase = GetEventsUseCase(fakeEventRepo),
            getTasksUseCase = GetTasksUseCase(fakeTaskRepo),
            getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepo)
        )

        val getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepo)
        val getCurrentUserUseCase = GetCurrentUserUseCase(fakeUserRepo)

        val useCase = SendAiMessageUseCase(
            repository = fakeRepository,
            localAiFallbackUseCase = localFallbackUseCase,
            getCurrentUidUseCase = getCurrentUidUseCase
        )

        viewModel = AiChatViewModel(
            sendAiMessageUseCase = useCase,
            getCurrentUidUseCase = getCurrentUidUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty messages and no loading`() {
        val state = viewModel.state.value
        assertTrue(state.messages.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("", state.inputText)
        assertEquals(AiChatStatus.IDLE, state.status)
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
    fun `sendMessage adds user message and gets greeting response`() = runTest {
        viewModel.onInputChanged("Hola")
        viewModel.sendMessage()

        val state = viewModel.state.value
        assertEquals(2, state.messages.size)
        assertEquals("Hola", state.messages[0].content)
        assertEquals(AiMessageRole.USER, state.messages[0].role)
        assertEquals("Hola, ¿en qué puedo ayudarte?", state.messages[1].content)
        assertEquals(AiMessageRole.ASSISTANT, state.messages[1].role)
        assertFalse(state.isLoading)
        assertEquals("", state.inputText)
        assertEquals(AiChatStatus.DETERMINISTIC_FALLBACK, state.status)
    }

    @Test
    fun `sendMessage with external query delegates to AI repository`() = runTest {
        fakeRepository.response = "Respuesta del modelo AI"
        viewModel.onInputChanged("Explícame cálculo integral")
        viewModel.sendMessage()

        val state = viewModel.state.value
        assertEquals(2, state.messages.size)
        assertEquals(AiMessageRole.USER, state.messages[0].role)
        assertEquals("Respuesta del modelo AI", state.messages[1].content)
        assertEquals(AiMessageRole.ASSISTANT, state.messages[1].role)
        assertEquals(AiChatStatus.AI_RESPONSE, state.status)
    }

    @Test
    fun `sendMessage when AI fails enters limited mode with friendly message`() = runTest {
        fakeRepository.shouldFail = true
        viewModel.onInputChanged("Explícame cálculo integral")
        viewModel.sendMessage()

        val state = viewModel.state.value
        assertEquals(2, state.messages.size)
        assertTrue(state.isLimitedMode)
        assertEquals(AiChatStatus.LIMITED_MODE, state.status)
        assertTrue(state.messages[1].content.contains("limitada") || state.messages[1].content.contains("problema"))
    }

    @Test
    fun `newChat resets state and messages`() = runTest {
        viewModel.onInputChanged("Hola")
        viewModel.sendMessage()
        assertEquals(2, viewModel.state.value.messages.size)

        viewModel.newChat()
        assertTrue(viewModel.state.value.messages.isEmpty())
        assertEquals(AiChatStatus.IDLE, viewModel.state.value.status)
        assertFalse(viewModel.state.value.isLimitedMode)
    }
}

class TestAiRepository : AiRepository {
    var response: String = "Test response"
    var shouldFail: Boolean = false

    override suspend fun sendMessage(message: String): Result<AiResponse> {
        return if (shouldFail) {
            Result.failure(Exception("Test error"))
        } else {
            Result.success(AiResponse(text = response, source = AiResponseSource.AI))
        }
    }
}
