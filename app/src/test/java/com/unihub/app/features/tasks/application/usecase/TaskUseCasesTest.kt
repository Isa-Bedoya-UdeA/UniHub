package com.unihub.app.features.tasks.application.usecase

import com.unihub.app.features.notifications.application.usecase.CancelReminderUseCase
import com.unihub.app.features.notifications.application.usecase.ScheduleTaskReminderUseCase
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskUseCasesTest {

    private val tasksStorage = mutableMapOf<String, Task>()
    private val scheduledReminders = mutableListOf<String>()
    private val cancelledReminders = mutableListOf<String>()

    private val fakeTaskRepository = object : TaskRepository {
        override fun getTasks(userId: String): Flow<List<Task>> =
            flowOf(tasksStorage.values.filter { it.userId == userId })

        override fun getTasksBySubject(subjectId: String): Flow<List<Task>> =
            flowOf(tasksStorage.values.filter { it.subjectId == subjectId })

        override fun getTaskById(id: String): Flow<Task?> =
            flowOf(tasksStorage[id])

        override suspend fun saveTask(task: Task) {
            tasksStorage[task.id] = task
        }

        override suspend fun updateTask(task: Task) {
            tasksStorage[task.id] = task
        }

        override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
            tasksStorage[id]?.let {
                tasksStorage[id] = it.copy(status = status)
            }
        }

        override suspend fun deleteTask(id: String) {
            tasksStorage.remove(id)
        }

        override suspend fun syncTasks(userId: String) {}
        override suspend fun saveTaskTags(userId: String, taskId: String, tagIds: List<String>) {}
        override suspend fun getTaskTags(userId: String, taskId: String): List<String> = emptyList()
    }

    private val fakeNotificationScheduler = object : NotificationScheduler {
        override fun schedule(reminder: Reminder) {
            scheduledReminders.add(reminder.id)
        }
        override fun cancel(reminderId: String) {
            cancelledReminders.add(reminderId)
        }
        override fun cancelAllForEntity(entityId: String) {
            cancelledReminders.add(entityId)
        }
        override fun hasExactAlarmPermission(): Boolean = true
    }

    private val scheduleTaskReminderUseCase = ScheduleTaskReminderUseCase(fakeNotificationScheduler)
    private val cancelReminderUseCase = CancelReminderUseCase(fakeNotificationScheduler)

    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var saveTaskUseCase: SaveTaskUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var updateTaskStatusUseCase: UpdateTaskStatusUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @Before
    fun setUp() {
        tasksStorage.clear()
        scheduledReminders.clear()
        cancelledReminders.clear()

        getTasksUseCase = GetTasksUseCase(fakeTaskRepository)
        getTaskByIdUseCase = GetTaskByIdUseCase(fakeTaskRepository)
        saveTaskUseCase = SaveTaskUseCase(fakeTaskRepository, scheduleTaskReminderUseCase)
        updateTaskUseCase = UpdateTaskUseCase(fakeTaskRepository, scheduleTaskReminderUseCase)
        updateTaskStatusUseCase = UpdateTaskStatusUseCase(fakeTaskRepository, scheduleTaskReminderUseCase)
        deleteTaskUseCase = DeleteTaskUseCase(fakeTaskRepository, cancelReminderUseCase)
    }

    @Test
    fun `saveTask stores task in repository`() = runBlocking {
        val task = Task(
            id = "t1",
            userId = "u1",
            academicPeriodId = "p1",
            subjectId = "s1",
            title = "Taller de Matemáticas",
            description = "Ejercicios 1 a 10",
            dueAt = "2026-10-15T23:59:00",
            priority = TaskPriority.HIGH,
            status = TaskStatus.PENDING,
            reminderType = null,
            reminderValue = null,
            isDeadlineReminderEnabled = false,
            createdAt = "2026-10-09T00:00:00",
            updatedAt = "2026-10-09T00:00:00"
        )

        saveTaskUseCase(task)

        assertEquals(1, tasksStorage.size)
        assertEquals("Taller de Matemáticas", tasksStorage["t1"]?.title)
        assertEquals(TaskPriority.HIGH, tasksStorage["t1"]?.priority)
    }

    @Test
    fun `updateTaskStatus updates status to COMPLETED`() = runBlocking {
        val task = Task(
            id = "t2",
            userId = "u1",
            academicPeriodId = null,
            subjectId = null,
            title = "Leer capítulo 3",
            description = null,
            dueAt = null,
            priority = TaskPriority.MEDIUM,
            status = TaskStatus.PENDING,
            reminderType = null,
            reminderValue = null,
            isDeadlineReminderEnabled = false,
            createdAt = "2026-10-09T00:00:00",
            updatedAt = "2026-10-09T00:00:00"
        )
        tasksStorage["t2"] = task

        updateTaskStatusUseCase("t2", TaskStatus.COMPLETED)

        assertEquals(TaskStatus.COMPLETED, tasksStorage["t2"]?.status)
    }

    @Test
    fun `deleteTask removes task and cancels reminders`() = runBlocking {
        val task = Task(
            id = "t3",
            userId = "u1",
            academicPeriodId = null,
            subjectId = null,
            title = "Preparar presentación",
            description = null,
            dueAt = null,
            priority = TaskPriority.LOW,
            status = TaskStatus.PENDING,
            reminderType = null,
            reminderValue = null,
            isDeadlineReminderEnabled = false,
            createdAt = "2026-10-09T00:00:00",
            updatedAt = "2026-10-09T00:00:00"
        )
        tasksStorage["t3"] = task

        deleteTaskUseCase("t3")

        assertNull(tasksStorage["t3"])
        assertTrue(cancelledReminders.contains("t3"))
    }
}
