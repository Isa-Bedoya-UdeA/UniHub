package com.unihub.app.features.tasks.application.usecase

import com.unihub.app.features.notifications.application.usecase.CancelReminderUseCase
import com.unihub.app.features.notifications.application.usecase.ScheduleTaskReminderUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(userId: String) = repository.getTasks(userId)
}

class GetTaskByIdUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(id: String) = repository.getTaskById(id)
}

class SaveTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val scheduleTaskReminderUseCase: ScheduleTaskReminderUseCase
) {
    suspend operator fun invoke(task: Task) {
        repository.saveTask(task)
        scheduleTaskReminderUseCase(task)
    }
}

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val scheduleTaskReminderUseCase: ScheduleTaskReminderUseCase
) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task)
        scheduleTaskReminderUseCase(task)
    }
}

class UpdateTaskStatusUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val scheduleTaskReminderUseCase: ScheduleTaskReminderUseCase
) {
    suspend operator fun invoke(id: String, status: TaskStatus) {
        repository.updateTaskStatus(id, status)
        repository.getTaskById(id).first()?.let { task ->
            scheduleTaskReminderUseCase(task)
        }
    }
}

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val cancelReminderUseCase: CancelReminderUseCase
) {
    suspend operator fun invoke(id: String) {
        repository.deleteTask(id)
        cancelReminderUseCase(id)
    }
}
