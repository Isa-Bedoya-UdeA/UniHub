package com.unihub.app.features.tasks.presentation.viewmodel

import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.notifications.application.usecase.CancelReminderUseCase
import com.unihub.app.features.notifications.application.usecase.ScheduleTaskReminderUseCase
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.tasks.application.usecase.DeleteTaskUseCase
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.application.usecase.UpdateTaskStatusUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tasksStorage = mutableMapOf<String, Task>()
    private val subjectsStorage = mutableMapOf<String, Subject>()

    private val fakeAuthRepository = object : AuthRepository {
        override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Authenticated("u1"))
        override fun getCurrentUid(): String = "u1"
        override suspend fun signInWithGoogle(idToken: String): Result<String> = Result.success("u1")
        override suspend fun signOut() {}
        override suspend fun syncExistingUser(userId: String) {}
    }

    private val fakeSubjectRepository = object : SubjectRepository {
        override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> = flowOf(subjectsStorage.values.toList())
        override fun getAllSubjects(userId: String): Flow<List<Subject>> = flowOf(subjectsStorage.values.toList())
        override fun getSubjectById(id: String): Flow<Subject?> = flowOf(subjectsStorage[id])
        override suspend fun saveSubject(subject: Subject) {}
        override suspend fun updateSubject(subject: Subject) {}
        override suspend fun deleteSubject(id: String) {}
        override suspend fun syncSubjects(userId: String) {}
    }

    private val fakeTaskRepository = object : TaskRepository {
        override fun getTasks(userId: String): Flow<List<Task>> = flowOf(tasksStorage.values.filter { it.userId == userId })
        override fun getTasksBySubject(subjectId: String): Flow<List<Task>> = flowOf(tasksStorage.values.filter { it.subjectId == subjectId })
        override fun getTaskById(id: String): Flow<Task?> = flowOf(tasksStorage[id])
        override suspend fun saveTask(task: Task) { tasksStorage[task.id] = task }
        override suspend fun updateTask(task: Task) { tasksStorage[task.id] = task }
        override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
            tasksStorage[id]?.let { tasksStorage[id] = it.copy(status = status) }
        }
        override suspend fun deleteTask(id: String) { tasksStorage.remove(id) }
        override suspend fun syncTasks(userId: String) {}
        override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {}
        override suspend fun getTaskTags(userId: String, taskId: String): List<String> = emptyList()
    }

    private val fakeScheduler = object : NotificationScheduler {
        override fun schedule(reminder: Reminder) {}
        override fun cancel(reminderId: String) {}
        override fun cancelAllForEntity(entityId: String) {}
        override fun hasExactAlarmPermission(): Boolean = true
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tasksStorage.clear()
        subjectsStorage.clear()

        subjectsStorage["sub1"] = Subject("sub1", "u1", "st1", "p1", "Programación Móvil", null, 3, null, null, null, false, "", "")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): TasksViewModel {
        val scheduleReminder = ScheduleTaskReminderUseCase(fakeScheduler)
        val cancelReminder = CancelReminderUseCase(fakeScheduler)

        return TasksViewModel(
            getTasksUseCase = GetTasksUseCase(fakeTaskRepository),
            getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepository),
            saveTaskUseCase = SaveTaskUseCase(fakeTaskRepository, scheduleReminder),
            updateTaskStatusUseCase = UpdateTaskStatusUseCase(fakeTaskRepository, scheduleReminder),
            deleteTaskUseCase = DeleteTaskUseCase(fakeTaskRepository, cancelReminder),
            getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepository)
        )
    }

    @Test
    fun `loadSubjectNames populates map of subject id to name`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val subjectNames = viewModel.subjectNames.value
        assertEquals("Programación Móvil", subjectNames["sub1"])
    }

    @Test
    fun `toggleTaskCompletion updates task status from PENDING to COMPLETED`() = runTest {
        val task = Task(
            id = "t1",
            userId = "u1",
            academicPeriodId = "p1",
            subjectId = "sub1",
            title = "App Final",
            description = null,
            dueAt = null,
            priority = TaskPriority.HIGH,
            status = TaskStatus.PENDING,
            reminderType = null,
            reminderValue = null,
            isDeadlineReminderEnabled = false,
            createdAt = "",
            updatedAt = ""
        )
        tasksStorage["t1"] = task

        val viewModel = createViewModel()
        viewModel.toggleTaskCompletion(task)
        advanceUntilIdle()

        assertEquals(TaskStatus.COMPLETED, tasksStorage["t1"]?.status)
    }

    @Test
    fun `removeTask deletes task from repository`() = runTest {
        val task = Task(
            id = "t2",
            userId = "u1",
            academicPeriodId = null,
            subjectId = null,
            title = "Tarea a borrar",
            description = null,
            dueAt = null,
            priority = TaskPriority.LOW,
            status = TaskStatus.PENDING,
            reminderType = null,
            reminderValue = null,
            isDeadlineReminderEnabled = false,
            createdAt = "",
            updatedAt = ""
        )
        tasksStorage["t2"] = task

        val viewModel = createViewModel()
        viewModel.removeTask("t2")
        advanceUntilIdle()

        assertEquals(0, tasksStorage.size)
    }
}
