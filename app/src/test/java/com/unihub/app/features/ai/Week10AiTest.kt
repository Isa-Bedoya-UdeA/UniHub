package com.unihub.app.features.ai

import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.application.usecase.SaveGradeUseCase
import com.unihub.app.features.academic.application.usecase.SaveSubjectUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.ai.application.usecase.ResolveAndExecuteAiActionUseCase
import com.unihub.app.features.ai.domain.model.AiActionItem
import com.unihub.app.features.ai.domain.model.AiActionType
import com.unihub.app.features.ai.domain.model.AiPendingAction
import com.unihub.app.features.ai.domain.model.AiStructuredResponse
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.events.application.usecase.SaveEventUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventTag
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.notifications.application.usecase.ScheduleEventReminderUseCase
import com.unihub.app.features.notifications.application.usecase.ScheduleTaskReminderUseCase
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class Week10AiTest {

    private lateinit var resolveAndExecuteUseCase: ResolveAndExecuteAiActionUseCase

    private val savedSubjects = mutableListOf<Subject>()
    private val savedTasks = mutableListOf<Task>()
    private val savedEvents = mutableListOf<Event>()
    private val savedGrades = mutableListOf<Grade>()

    private val studyList = mutableListOf<Study>()
    private val periodList = mutableListOf<AcademicPeriod>()
    private val subjectList = mutableListOf<Subject>()

    @Before
    fun setUp() {
        savedSubjects.clear()
        savedTasks.clear()
        savedEvents.clear()
        savedGrades.clear()
        studyList.clear()
        periodList.clear()
        subjectList.clear()

        val testStudy = Study(
            id = "study_1",
            userId = "user_1",
            name = "Ingeniería de Sistemas",
            institution = "UdeA",
            totalCredits = 160,
            isActive = true,
            createdAt = "",
            updatedAt = ""
        )
        studyList.add(testStudy)

        val testPeriod = AcademicPeriod(
            id = "period_1",
            userId = "user_1",
            studyId = "study_1",
            name = "2025-1",
            startDate = "2025-02-01",
            endDate = "2025-06-30",
            isCurrent = true,
            createdAt = "",
            updatedAt = ""
        )
        periodList.add(testPeriod)

        val testSubject = Subject(
            id = "subject_1",
            userId = "user_1",
            studyId = "study_1",
            academicPeriodId = "period_1",
            name = "Algoritmos",
            code = "ALG101",
            credits = 4,
            professor = "Prof. García",
            color = "#3F51B5",
            notes = null,
            isCompleted = false,
            createdAt = "",
            updatedAt = ""
        )
        subjectList.add(testSubject)

        val fakeAuthRepo = object : AuthRepository {
            override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Authenticated("user_1"))
            override fun getCurrentUid(): String = "user_1"
            override suspend fun signInWithGoogle(idToken: String): Result<String> = Result.success("ok")
            override suspend fun signOut() {}
            override suspend fun syncExistingUser(userId: String) {}
        }

        val fakeStudyRepo = object : StudyRepository {
            override fun getStudies(userId: String): Flow<List<Study>> = flowOf(studyList)
            override fun getStudyById(id: String): Flow<Study?> = flowOf(studyList.find { it.id == id })
            override suspend fun saveStudy(study: Study) {}
            override suspend fun deleteStudy(id: String) {}
            override suspend fun setActiveStudy(userId: String, studyId: String) {}
            override suspend fun syncStudies(userId: String) {}
        }

        val fakeAcademicRepo = object : AcademicRepository {
            override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> = flowOf(periodList)
            override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> = flowOf(periodList)
            override suspend fun saveAcademicPeriod(period: AcademicPeriod) {}
            override suspend fun updateAcademicPeriod(period: AcademicPeriod) {}
            override suspend fun deleteAcademicPeriod(id: String) {}
            override suspend fun setCurrentPeriod(userId: String, id: String) {}
            override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> = flowOf(savedGrades.filter { it.subjectId == subjectId })
            override suspend fun saveGrade(grade: Grade) { savedGrades.add(grade) }
            override suspend fun updateGrade(grade: Grade) {}
            override suspend fun deleteGrade(id: String) {}
            override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> = flowOf(AcademicSummary(4.2, 4.2, 80, 160, 50.0))
            override suspend fun syncAcademicData(userId: String) {}
        }

        val fakeSubjectRepo = object : SubjectRepository {
            override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> = flowOf(subjectList)
            override fun getAllSubjects(userId: String): Flow<List<Subject>> = flowOf(subjectList)
            override fun getSubjectById(id: String): Flow<Subject?> = flowOf(subjectList.find { it.id == id })
            override suspend fun saveSubject(subject: Subject) { savedSubjects.add(subject) }
            override suspend fun updateSubject(subject: Subject) {}
            override suspend fun deleteSubject(id: String) {}
            override suspend fun syncSubjects(userId: String) {}
        }

        val fakeScheduler = object : NotificationScheduler {
            override fun schedule(reminder: Reminder) {}
            override fun cancel(reminderId: String) {}
            override fun cancelAllForEntity(entityId: String) {}
            override fun hasExactAlarmPermission(): Boolean = true
        }

        val fakeTaskRepo = object : TaskRepository {
            override fun getTasks(userId: String): Flow<List<Task>> = flowOf(savedTasks)
            override fun getTasksBySubject(subjectId: String): Flow<List<Task>> = flowOf(savedTasks)
            override fun getTaskById(id: String): Flow<Task?> = flowOf(null)
            override suspend fun saveTask(task: Task) { savedTasks.add(task) }
            override suspend fun updateTask(task: Task) {}
            override suspend fun deleteTask(id: String) {}
            override suspend fun updateTaskStatus(id: String, status: TaskStatus) {}
            override suspend fun syncTasks(userId: String) {}
            override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {}
            override suspend fun getTaskTags(userId: String, taskId: String): List<String> = emptyList()
        }

        val fakeEventRepo = object : EventRepository {
            override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> = flowOf(savedEvents)
            override fun getAllEvents(userId: String): Flow<List<Event>> = flowOf(savedEvents)
            override fun getEventById(id: String): Flow<Event?> = flowOf(null)
            override suspend fun saveEvent(event: Event) { savedEvents.add(event) }
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

        resolveAndExecuteUseCase = ResolveAndExecuteAiActionUseCase(
            getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepo),
            getStudiesUseCase = GetStudiesUseCase(fakeStudyRepo),
            getAcademicPeriodsUseCase = GetAcademicPeriodsUseCase(fakeAcademicRepo),
            getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepo),
            saveSubjectUseCase = SaveSubjectUseCase(fakeSubjectRepo),
            saveTaskUseCase = SaveTaskUseCase(fakeTaskRepo, ScheduleTaskReminderUseCase(fakeScheduler)),
            saveEventUseCase = SaveEventUseCase(fakeEventRepo, ScheduleEventReminderUseCase(fakeEventRepo, fakeScheduler)),
            saveGradeUseCase = SaveGradeUseCase(fakeAcademicRepo)
        )
    }

    // 1. One subject creation
    @Test
    fun `resolve single subject infers study and period`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_SUBJECT,
            message = "Voy a crear Bases de Datos",
            requiresConfirmation = true,
            items = listOf(AiActionItem(name = "Bases de Datos"))
        )

        val resolved = resolveAndExecuteUseCase.resolve(input)
        assertEquals(AiActionType.CREATE_SUBJECT, resolved.type)
        assertTrue(resolved.requiresConfirmation)
        assertEquals("study_1", resolved.items[0].studyId)
        assertEquals("period_1", resolved.items[0].academicPeriodId)
    }

    // 2. Multiple subjects creation
    @Test
    fun `execute multiple subjects saves all subjects via UseCase`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_SUBJECT,
            message = "Creando 3 materias",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(name = "Bases de Datos", studyId = "study_1", academicPeriodId = "period_1"),
                AiActionItem(name = "Sistemas Operativos", studyId = "study_1", academicPeriodId = "period_1"),
                AiActionItem(name = "Redes", studyId = "study_1", academicPeriodId = "period_1")
            )
        )

        val pending = AiPendingAction(id = "p1", structuredResponse = input)
        val result = resolveAndExecuteUseCase.execute(pending)

        assertTrue(result.isSuccess)
        assertEquals(3, savedSubjects.size)
        assertEquals("Bases de Datos", savedSubjects[0].name)
        assertEquals("Sistemas Operativos", savedSubjects[1].name)
        assertEquals("Redes", savedSubjects[2].name)
    }

    // 3. One task creation
    @Test
    fun `resolve single task resolves subject ID from subjectName`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_TASK,
            message = "Tarea para Algoritmos",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(
                    title = "Entregar taller",
                    subjectName = "Algoritmos",
                    dueAt = "mañana 18:00"
                )
            )
        )

        val resolved = resolveAndExecuteUseCase.resolve(input)
        assertEquals(AiActionType.CREATE_TASK, resolved.type)
        assertEquals("subject_1", resolved.items[0].subjectId)
    }

    // 4. Multiple tasks creation
    @Test
    fun `execute multiple tasks saves all tasks via UseCase`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_TASK,
            message = "Creando 2 tareas",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(title = "Estudiar para examen", subjectId = "subject_1", dueAt = "2025-05-20 10:00"),
                AiActionItem(title = "Enviar reporte", subjectId = "subject_1", dueAt = "2025-05-22 23:59")
            )
        )

        val pending = AiPendingAction(id = "p2", structuredResponse = input)
        val result = resolveAndExecuteUseCase.execute(pending)

        assertTrue(result.isSuccess)
        assertEquals(2, savedTasks.size)
        assertEquals("Estudiar para examen", savedTasks[0].title)
        assertEquals("Enviar reporte", savedTasks[1].title)
    }

    // 5. One event creation & 6. Multiple events
    @Test
    fun `execute multiple events creates all events via UseCase`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_EVENT,
            message = "Creando 2 eventos",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(title = "Clase Algoritmos", subjectId = "subject_1", startAt = "2025-05-20 08:00", endAt = "2025-05-20 10:00"),
                AiActionItem(title = "Reunión de grupo", subjectId = "subject_1", startAt = "2025-05-21 14:00", endAt = "2025-05-21 15:00")
            )
        )

        val pending = AiPendingAction(id = "p3", structuredResponse = input)
        val result = resolveAndExecuteUseCase.execute(pending)

        assertTrue(result.isSuccess)
        assertEquals(2, savedEvents.size)
        assertEquals("Clase Algoritmos", savedEvents[0].title)
        assertEquals("Reunión de grupo", savedEvents[1].title)
    }

    // 7. Register grade & 8. Multiple grades
    @Test
    fun `execute register multiple grades saves grades via UseCase`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.REGISTER_GRADE,
            message = "Registrando notas",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(name = "Parcial 1", subjectId = "subject_1", academicPeriodId = "period_1", value = 4.5, weight = 25.0),
                AiActionItem(name = "Taller 1", subjectId = "subject_1", academicPeriodId = "period_1", value = 4.0, weight = 15.0)
            )
        )

        val pending = AiPendingAction(id = "p4", structuredResponse = input)
        val result = resolveAndExecuteUseCase.execute(pending)

        assertTrue(result.isSuccess)
        assertEquals(2, savedGrades.size)
        assertEquals(4.5, savedGrades[0].value, 0.01)
        assertEquals(4.0, savedGrades[1].value, 0.01)
    }

    // 9. Validation: Invalid grade value
    @Test
    fun `resolve grade with invalid value triggers clarification`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.REGISTER_GRADE,
            message = "Registrando nota",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(name = "Parcial", subjectName = "Algoritmos", value = 8.0)
            )
        )

        val resolved = resolveAndExecuteUseCase.resolve(input)
        assertEquals(AiActionType.CLARIFICATION, resolved.type)
        assertFalse(resolved.requiresConfirmation)
        assertTrue(resolved.missingFields.contains("valor_nota_valido_0_a_5"))
    }

    // 10. Validation: Event end time before start time
    @Test
    fun `resolve event with end time before start time adjusts end time`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_EVENT,
            message = "Creando evento",
            requiresConfirmation = true,
            items = listOf(
                AiActionItem(title = "Clase", startAt = "2025-05-20 10:00", endAt = "2025-05-20 08:00")
            )
        )

        val resolved = resolveAndExecuteUseCase.resolve(input)
        assertEquals(AiActionType.CREATE_EVENT, resolved.type)
        assertTrue(resolved.items[0].endAt!! > resolved.items[0].startAt!!)
    }

    // 11. Missing required field -> trigger clarification
    @Test
    fun `resolve subject with blank name triggers clarification`() = runBlocking {
        val input = AiStructuredResponse(
            type = AiActionType.CREATE_SUBJECT,
            message = "Creando materia",
            requiresConfirmation = true,
            items = listOf(AiActionItem(name = ""))
        )

        val resolved = resolveAndExecuteUseCase.resolve(input)
        assertEquals(AiActionType.CLARIFICATION, resolved.type)
        assertTrue(resolved.missingFields.contains("nombre_materia"))
    }
}
