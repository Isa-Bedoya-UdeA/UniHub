package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.ai.domain.model.AiResponse
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.*
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class SendAiMessageUseCaseTest {

    private lateinit var useCase: SendAiMessageUseCase
    private lateinit var fakeRepository: FakeAiRepository
    private var eventsList: List<Event> = emptyList()
    private var tasksList: List<Task> = emptyList()

    @Before
    fun setUp() {
        fakeRepository = FakeAiRepository()
        eventsList = emptyList()
        tasksList = emptyList()

        val fakeEventRepo = object : EventRepository {
            override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> = flowOf(eventsList)
            override fun getAllEvents(userId: String): Flow<List<Event>> = flowOf(eventsList)
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
            override fun getTasks(userId: String): Flow<List<Task>> = flowOf(tasksList)
            override fun getTasksBySubject(subjectId: String): Flow<List<Task>> = flowOf(tasksList)
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

        val localFallbackUseCase = LocalAiFallbackUseCase(
            getEventsUseCase = GetEventsUseCase(fakeEventRepo),
            getTasksUseCase = GetTasksUseCase(fakeTaskRepo),
            getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepo)
        )

        val getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepo)

        useCase = SendAiMessageUseCase(
            repository = fakeRepository,
            localAiFallbackUseCase = localFallbackUseCase,
            getCurrentUidUseCase = getCurrentUidUseCase
        )
    }

    @Test
    fun `greeting message returns greeting directly`() = runBlocking {
        val result = useCase("Hola")
        assertTrue(result.isSuccess)
        assertEquals("Hola, ¿en qué puedo ayudarte?", result.getOrNull()?.text)
        assertEquals(AiResponseSource.LOCAL_FALLBACK, result.getOrNull()?.source)
    }

    @Test
    fun `events query with no events returns expected message`() = runBlocking {
        val result = useCase("¿Qué eventos tengo?")
        assertTrue(result.isSuccess)
        assertEquals("No tienes eventos programados para hoy o mañana.", result.getOrNull()?.text)
        assertEquals(AiResponseSource.LOCAL_FALLBACK, result.getOrNull()?.source)
    }

    @Test
    fun `tasks query with no tasks returns expected message`() = runBlocking {
        val result = useCase("¿Qué tareas tengo?")
        assertTrue(result.isSuccess)
        assertEquals("No tienes tareas pendientes para hoy o mañana.", result.getOrNull()?.text)
        assertEquals(AiResponseSource.LOCAL_FALLBACK, result.getOrNull()?.source)
    }

    @Test
    fun `tasks query excludes completed tasks`() = runBlocking {
        val today = LocalDate.now(ZoneId.systemDefault()).toString()
        tasksList = listOf(
            Task(
                id = "1", userId = "u1", academicPeriodId = null, subjectId = null,
                title = "Tarea Terminada", description = null, dueAt = "$today 12:00",
                priority = TaskPriority.HIGH, status = TaskStatus.COMPLETED,
                reminderType = null, reminderValue = null, isDeadlineReminderEnabled = false,
                createdAt = "", updatedAt = ""
            )
        )
        val result = useCase("¿Qué tareas tengo?")
        assertTrue(result.isSuccess)
        assertEquals("No tienes tareas pendientes para hoy o mañana.", result.getOrNull()?.text)
    }

    @Test
    fun `tasks query includes pending tasks`() = runBlocking {
        val today = LocalDate.now(ZoneId.systemDefault()).toString()
        tasksList = listOf(
            Task(
                id = "1", userId = "u1", academicPeriodId = null, subjectId = null,
                title = "Taller de Bases de Datos", description = null, dueAt = "$today 10:30",
                priority = TaskPriority.HIGH, status = TaskStatus.PENDING,
                reminderType = null, reminderValue = null, isDeadlineReminderEnabled = false,
                createdAt = "", updatedAt = ""
            )
        )
        val result = useCase("¿Qué tareas tengo?")
        assertTrue(result.isSuccess)
        val text = result.getOrNull()?.text ?: ""
        assertTrue(text.contains("Taller de Bases de Datos"))
        assertTrue(text.contains("Pendiente"))
    }

    @Test
    fun `daily plan query with no items returns expected message`() = runBlocking {
        val result = useCase("¿Cuál es mi plan del día?")
        assertTrue(result.isSuccess)
        assertEquals("No tienes eventos ni tareas pendientes para hoy o mañana.", result.getOrNull()?.text)
    }

    @Test
    fun `daily plan combines events and incomplete tasks chronologically`() = runBlocking {
        val today = LocalDate.now(ZoneId.systemDefault()).toString()
        eventsList = listOf(
            Event(
                id = "e1", userId = "u1", academicPeriodId = null, subjectId = null,
                locationId = null, recurrenceRuleId = null, title = "Clase de Cálculo",
                startAt = "${today}T08:00:00", endAt = "${today}T10:00:00",
                locationType = LocationType.PHYSICAL, eventType = EventType.CLASS,
                meetingUrl = null, notes = "Aula 101", reminders = emptyList(),
                createdAt = "", updatedAt = ""
            )
        )
        tasksList = listOf(
            Task(
                id = "t1", userId = "u1", academicPeriodId = null, subjectId = null,
                title = "Entrega de Proyecto", description = null, dueAt = "${today}T14:00:00",
                priority = TaskPriority.HIGH, status = TaskStatus.IN_PROGRESS,
                reminderType = null, reminderValue = null, isDeadlineReminderEnabled = false,
                createdAt = "", updatedAt = ""
            )
        )
        val result = useCase("organiza mi día")
        assertTrue(result.isSuccess)
        val text = result.getOrNull()?.text ?: ""
        assertTrue(text.contains("Clase de Cálculo"))
        assertTrue(text.contains("Entrega de Proyecto"))
        assertTrue(text.indexOf("Clase de Cálculo") < text.indexOf("Entrega de Proyecto"))
    }

    @Test
    fun `invoke with blank message returns failure`() = runBlocking {
        val result = useCase("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke with message exceeding max length returns failure`() = runBlocking {
        val longMessage = "a".repeat(SendAiMessageUseCase.MAX_MESSAGE_LENGTH + 1)
        val result = useCase(longMessage)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `general query delegates to AI repository`() = runBlocking {
        fakeRepository.response = "La física cuántica estudia partículas subatómicas."
        val result = useCase("Explícame la física cuántica")
        assertTrue(result.isSuccess)
        assertEquals("La física cuántica estudia partículas subatómicas.", result.getOrNull()?.text)
        assertEquals(AiResponseSource.AI, result.getOrNull()?.source)
    }

    @Test
    fun `general query returns friendly fallback when AI fails`() = runBlocking {
        fakeRepository.shouldFail = true
        val result = useCase("Explícame la física cuántica")
        assertTrue(result.isSuccess)
        val text = result.getOrNull()?.text ?: ""
        assertTrue(text.contains("limitada") || text.contains("problema"))
        assertEquals(AiResponseSource.LIMITED_MODE, result.getOrNull()?.source)
    }
}

class FakeAiRepository : AiRepository {
    var response: String = ""
    var shouldFail: Boolean = false

    override suspend fun sendMessage(message: String): Result<AiResponse> {
        return if (shouldFail) {
            Result.failure(Exception("Fake error"))
        } else {
            Result.success(AiResponse(text = response, source = AiResponseSource.AI))
        }
    }
}
