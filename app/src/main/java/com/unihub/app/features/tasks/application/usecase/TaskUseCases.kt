package com.unihub.app.features.tasks.application.usecase

import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(userId: String) = repository.getTasks(userId)
}

class GetTaskByIdUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(id: String) = repository.getTaskById(id)
}

class SaveTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.saveTask(task)
}

class UpdateTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}

class UpdateTaskStatusUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(id: String, status: TaskStatus) = repository.updateTaskStatus(id, status)
}

class DeleteTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(id: String) = repository.deleteTask(id)
}
