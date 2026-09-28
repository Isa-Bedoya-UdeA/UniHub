package com.unihub.app.features.dashboard.presentation.viewmodel

import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.dashboard.application.usecase.GetDashboardDataUseCase
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.*
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.subjects.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.notifications.application.usecase.ScheduleTaskReminderUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `initial state loads dashboard data`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.state.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `getSubjectName returns subject name when found`() = runTest {
        val viewModel = createViewModel(subjects = listOf(
            Subject("s1", "u1", "study1", "p1", "Matemáticas", null, null, null, null, null, "", "")
        ))
        assertEquals("Matemáticas", viewModel.getSubjectName("s1"))
    }

    @Test
    fun `getSubjectName returns default when subject not found`() = runTest {
        val viewModel = createViewModel()
        assertEquals("Sin materia", viewModel.getSubjectName("nonexistent"))
    }

    @Test
    fun `getSubjectName returns default when subjectId is null`() = runTest {
        val viewModel = createViewModel()
        assertEquals("Sin materia", viewModel.getSubjectName(null))
    }

    @Test
    fun `clearError clears error message`() = runTest {
        val viewModel = createViewModel()
        viewModel.clearError()
        assertNull(viewModel.state.value.errorMessage)
    }

    private fun createViewModel(
        subjects: List<Subject> = emptyList()
    ): DashboardViewModel {
        val studyRepository = object : StudyRepository {
            override fun getStudies(userId: String): Flow<List<Study>> = flowOf(
                listOf(Study("study1", userId, "Test Study", "UDEA", 160, 60, 3.5, true, "", ""))
            )
            override fun getStudyById(id: String): Flow<Study?> = flowOf(null)
            override suspend fun saveStudy(study: Study) {}
            override suspend fun deleteStudy(id: String) {}
            override suspend fun setActiveStudy(userId: String, studyId: String) {}
            override suspend fun syncStudies(userId: String) {}
        }

        val academicRepository = object : AcademicRepository {
            override fun getAcademicPeriods(userId: String) = flowOf(emptyList<com.unihub.app.features.academic.domain.model.AcademicPeriod>())
            override fun getAcademicPeriodsByStudy(userId: String, studyId: String) = flowOf(emptyList<com.unihub.app.features.academic.domain.model.AcademicPeriod>())
            override suspend fun saveAcademicPeriod(period: com.unihub.app.features.academic.domain.model.AcademicPeriod) {}
            override suspend fun updateAcademicPeriod(period: com.unihub.app.features.academic.domain.model.AcademicPeriod) {}
            override suspend fun deleteAcademicPeriod(id: String) {}
            override suspend fun setCurrentPeriod(userId: String, id: String) {}
            override fun getGradesBySubject(subjectId: String) = flowOf(emptyList<com.unihub.app.features.academic.domain.model.Grade>())
            override suspend fun saveGrade(grade: com.unihub.app.features.academic.domain.model.Grade) {}
            override suspend fun updateGrade(grade: com.unihub.app.features.academic.domain.model.Grade) {}
            override suspend fun deleteGrade(id: String) {}
            override fun getAcademicSummary(userId: String, studyId: String) = flowOf(
                AcademicSummary(3.5, 3.5, 60, 160, 37.5)
            )
            override suspend fun syncAcademicData(userId: String) {}
        }

        val eventRepository = object : EventRepository {
            override fun getEvents(userId: String, start: String, end: String) = flowOf(emptyList<Event>())
            override fun getAllEvents(userId: String) = flowOf(emptyList<Event>())
            override fun getEventById(id: String) = flowOf(null)
            override suspend fun saveEvent(event: Event) {}
            override suspend fun updateEvent(event: Event) {}
            override suspend fun deleteEvent(id: String) {}
            override suspend fun syncEvents(userId: String) {}
            override fun getRemindersForEvent(eventId: String) = flowOf(emptyList<EventReminder>())
            override suspend fun saveReminders(eventId: String, reminders: List<EventReminder>) {}
            override suspend fun deleteRemindersForEvent(eventId: String) {}
            override fun getAllEnabledReminders() = flowOf(emptyList<EventReminder>())
            override fun getRecurrenceRule(ruleId: String) = flowOf(null)
            override fun getRecurrenceDays(ruleId: String) = flowOf(emptyList<RecurrenceDay>())
            override suspend fun saveRecurrenceRule(rule: RecurrenceRule) {}
            override suspend fun saveRecurrenceDay(day: RecurrenceDay) {}
            override suspend fun deleteRecurrenceRule(id: String) {}
            override suspend fun deleteRecurrenceDaysByRule(ruleId: String) {}
            override fun getEventTags(eventId: String) = flowOf(emptyList<EventTag>())
            override suspend fun saveEventTag(eventTag: EventTag) {}
            override suspend fun deleteEventTag(eventId: String, tagId: String) {}
            override suspend fun deleteEventTagsForEvent(eventId: String) {}
        }

        val taskRepository = object : TaskRepository {
            override fun getTasks(userId: String) = flowOf(emptyList<Task>())
            override fun getTasksBySubject(subjectId: String) = flowOf(emptyList<Task>())
            override fun getTaskById(id: String) = flowOf(null)
            override suspend fun saveTask(task: Task) {}
            override suspend fun updateTask(task: Task) {}
            override suspend fun deleteTask(id: String) {}
            override suspend fun updateTaskStatus(id: String, status: com.unihub.app.features.tasks.domain.model.TaskStatus) {}
            override suspend fun syncTasks(userId: String) {}
            override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {}
            override suspend fun getTaskTags(userId: String, taskId: String) = emptyList<String>()
        }

        val subjectRepository = object : SubjectRepository {
            override fun getSubjects(userId: String, academicPeriodId: String) = flowOf(subjects)
            override fun getAllSubjects(userId: String) = flowOf(subjects)
            override fun getSubjectById(id: String) = flowOf(subjects.firstOrNull())
            override suspend fun saveSubject(subject: Subject) {}
            override suspend fun updateSubject(subject: Subject) {}
            override suspend fun deleteSubject(id: String) {}
            override suspend fun syncSubjects(userId: String) {}
        }

        val authRepository = object : AuthRepository {
            override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Authenticated("test_uid"))
            override suspend fun signInWithGoogle(idToken: String) = Result.success("test_uid")
            override suspend fun signOut() {}
            override fun getCurrentUid(): String = "test_uid"
            override suspend fun syncExistingUser(userId: String) {}
        }

        val notificationScheduler = object : NotificationScheduler {
            override fun schedule(reminder: Reminder) {}
            override fun cancel(reminderId: String) {}
            override fun cancelAllForEntity(entityId: String) {}
            override fun hasExactAlarmPermission() = true
        }

        val getStudiesUseCase = GetStudiesUseCase(studyRepository)
        val getAcademicSummaryUseCase = GetAcademicSummaryUseCase(academicRepository)
        val getEventsUseCase = GetEventsUseCase(eventRepository)
        val getTasksUseCase = GetTasksUseCase(taskRepository)
        val getAllSubjectsUseCase = GetAllSubjectsUseCase(subjectRepository)
        val getCurrentUidUseCase = GetCurrentUidUseCase(authRepository)

        val scheduleTaskReminderUseCase = ScheduleTaskReminderUseCase(notificationScheduler)
        val updateTaskStatusUseCase = UpdateTaskStatusUseCase(taskRepository, scheduleTaskReminderUseCase)

        val getDashboardDataUseCase = GetDashboardDataUseCase(
            getAcademicSummaryUseCase,
            getStudiesUseCase,
            getEventsUseCase,
            getTasksUseCase
        )

        return DashboardViewModel(
            getDashboardDataUseCase,
            updateTaskStatusUseCase,
            getCurrentUidUseCase,
            getAllSubjectsUseCase
        )
    }
}
