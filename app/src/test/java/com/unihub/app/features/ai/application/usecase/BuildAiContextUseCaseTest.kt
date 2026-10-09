package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildAiContextUseCaseTest {

    private val studies = mutableListOf<Study>()
    private val periods = mutableListOf<AcademicPeriod>()
    private val subjects = mutableListOf<Subject>()
    private val grades = mutableListOf<Grade>()
    private val tasks = mutableListOf<Task>()
    private val events = mutableListOf<Event>()

    private val fakeAuthRepository = object : AuthRepository {
        override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Authenticated("u1"))
        override fun getCurrentUid(): String = "u1"
        override suspend fun signInWithGoogle(idToken: String): Result<String> = Result.success("u1")
        override suspend fun signOut() {}
        override suspend fun syncExistingUser(userId: String) {}
    }

    private val fakeStudyRepository = object : StudyRepository {
        override fun getStudies(userId: String): Flow<List<Study>> = flowOf(studies)
        override fun getStudyById(id: String): Flow<Study?> = flowOf(studies.find { it.id == id })
        override suspend fun saveStudy(study: Study) {}
        override suspend fun deleteStudy(id: String) {}
        override suspend fun setActiveStudy(userId: String, studyId: String) {}
        override suspend fun syncStudies(userId: String) {}
    }

    private val fakeAcademicRepository = object : AcademicRepository {
        override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> = flowOf(periods)
        override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> = flowOf(periods)
        override suspend fun saveAcademicPeriod(period: AcademicPeriod) {}
        override suspend fun updateAcademicPeriod(period: AcademicPeriod) {}
        override suspend fun deleteAcademicPeriod(id: String) {}
        override suspend fun setCurrentPeriod(userId: String, id: String) {}
        override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> = flowOf(grades.filter { it.subjectId == subjectId })
        override suspend fun saveGrade(grade: Grade) {}
        override suspend fun updateGrade(grade: Grade) {}
        override suspend fun deleteGrade(id: String) {}
        override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> =
            flowOf(AcademicSummary(cumulativeGpa = 4.1, currentSemesterGpa = 4.3, earnedCredits = 45, targetCredits = 160, progressPercentage = 28.125))
        override suspend fun syncAcademicData(userId: String) {}
    }

    private val fakeSubjectRepository = object : SubjectRepository {
        override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> = flowOf(subjects)
        override fun getAllSubjects(userId: String): Flow<List<Subject>> = flowOf(subjects)
        override fun getSubjectById(id: String): Flow<Subject?> = flowOf(subjects.find { it.id == id })
        override suspend fun saveSubject(subject: Subject) {}
        override suspend fun updateSubject(subject: Subject) {}
        override suspend fun deleteSubject(id: String) {}
        override suspend fun syncSubjects(userId: String) {}
    }

    private val fakeEventRepository = object : EventRepository {
        override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> = flowOf(events)
        override fun getAllEvents(userId: String): Flow<List<Event>> = flowOf(events)
        override fun getEventById(id: String): Flow<Event?> = flowOf(events.find { it.id == id })
        override suspend fun saveEvent(event: Event) {}
        override suspend fun updateEvent(event: Event) {}
        override suspend fun deleteEvent(id: String) {}
        override suspend fun syncEvents(userId: String) {}

        override fun getRemindersForEvent(eventId: String): Flow<List<com.unihub.app.features.events.domain.model.EventReminder>> = flowOf(emptyList())
        override suspend fun saveReminders(eventId: String, reminders: List<com.unihub.app.features.events.domain.model.EventReminder>) {}
        override suspend fun deleteRemindersForEvent(eventId: String) {}
        override fun getAllEnabledReminders(): Flow<List<com.unihub.app.features.events.domain.model.EventReminder>> = flowOf(emptyList())

        override fun getRecurrenceRule(ruleId: String): Flow<com.unihub.app.features.events.domain.model.RecurrenceRule?> = flowOf(null)
        override fun getRecurrenceDays(ruleId: String): Flow<List<com.unihub.app.features.events.domain.model.RecurrenceDay>> = flowOf(emptyList())
        override suspend fun saveRecurrenceRule(rule: com.unihub.app.features.events.domain.model.RecurrenceRule) {}
        override suspend fun saveRecurrenceDay(day: com.unihub.app.features.events.domain.model.RecurrenceDay) {}
        override suspend fun deleteRecurrenceRule(id: String) {}
        override suspend fun deleteRecurrenceDaysByRule(ruleId: String) {}

        override fun getEventTags(eventId: String): Flow<List<com.unihub.app.features.events.domain.model.EventTag>> = flowOf(emptyList())
        override suspend fun saveEventTag(eventTag: com.unihub.app.features.events.domain.model.EventTag) {}
        override suspend fun deleteEventTag(eventId: String, tagId: String) {}
        override suspend fun deleteEventTagsForEvent(eventId: String) {}
    }

    private val fakeTaskRepository = object : TaskRepository {
        override fun getTasks(userId: String): Flow<List<Task>> = flowOf(tasks)
        override fun getTasksBySubject(subjectId: String): Flow<List<Task>> = flowOf(tasks)
        override fun getTaskById(id: String): Flow<Task?> = flowOf(tasks.find { it.id == id })
        override suspend fun saveTask(task: Task) {}
        override suspend fun updateTask(task: Task) {}
        override suspend fun updateTaskStatus(id: String, status: TaskStatus) {}
        override suspend fun deleteTask(id: String) {}
        override suspend fun syncTasks(userId: String) {}
        override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {}
        override suspend fun getTaskTags(userId: String, taskId: String): List<String> = emptyList()
    }

    private lateinit var buildAiContextUseCase: BuildAiContextUseCase

    @Before
    fun setUp() {
        studies.clear()
        periods.clear()
        subjects.clear()
        grades.clear()
        tasks.clear()
        events.clear()

        buildAiContextUseCase = BuildAiContextUseCase(
            getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepository),
            getStudiesUseCase = GetStudiesUseCase(fakeStudyRepository),
            getAcademicPeriodsUseCase = GetAcademicPeriodsUseCase(fakeAcademicRepository),
            getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepository),
            getEventsUseCase = GetEventsUseCase(fakeEventRepository),
            getTasksUseCase = GetTasksUseCase(fakeTaskRepository),
            getGradesBySubjectUseCase = GetGradesBySubjectUseCase(fakeAcademicRepository),
            getAcademicSummaryUseCase = GetAcademicSummaryUseCase(fakeAcademicRepository)
        )
    }

    @Test
    fun `buildAiContext outputs formatted academic summary and subjects`() = runBlocking {
        studies.add(Study("st1", "u1", "Ingeniería de Sistemas", "Universidad de Antioquia", 160, 45, 4.1, true, "", ""))
        periods.add(AcademicPeriod("p1", "u1", "st1", "2026-1", "2026-02-01", "2026-06-30", true, "", ""))
        subjects.add(Subject("sub1", "u1", "st1", "p1", "Bases de Datos", "BD-201", 3, "Prof. Codd", "#0000FF", null, false, "", ""))
        grades.add(Grade("g1", "u1", "sub1", "p1", "Examen 1", 4.5, 0.25, null, "", ""))
        tasks.add(Task("t1", "u1", "p1", "sub1", "Tarea SQL", null, "2026-10-15", TaskPriority.HIGH, TaskStatus.PENDING, null, null, false, "", ""))
        events.add(Event("e1", "u1", "p1", "sub1", null, null, "Clase SQL", "2026-10-10T10:00:00", "2026-10-10T12:00:00", LocationType.PHYSICAL, EventType.CLASS, null, null, emptyList(), null, emptyList(), "", ""))

        val context = buildAiContextUseCase()

        assertTrue(context.contains("FECHA Y HORA ACTUAL"))
        assertTrue(context.contains("Ingeniería de Sistemas"))
        assertTrue(context.contains("2026-1"))
        assertTrue(context.contains("Bases de Datos"))
        assertTrue(context.contains("Examen 1"))
        assertTrue(context.contains("Tarea SQL"))
        assertTrue(context.contains("Clase SQL"))
    }
}
