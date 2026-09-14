package com.unihub.app.features.notifications.application.usecase

import com.unihub.app.core.util.DateUtils
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.model.ReminderType
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import java.time.LocalDateTime
import javax.inject.Inject

class ScheduleTaskReminderUseCase @Inject constructor(
    private val notificationScheduler: NotificationScheduler
) {
    operator fun invoke(task: Task) {
        notificationScheduler.cancelAllForEntity(task.id)
        
        if (task.status == TaskStatus.COMPLETED) return

        val now = DateUtils.getCurrentTime()

        // Task reminder
        if (task.reminderAt != null) {
            val reminderTime = DateUtils.parseDateTime(task.reminderAt)
            if (reminderTime.isAfter(now)) {
                notificationScheduler.schedule(
                    Reminder(
                        id = "TASK_${task.id}",
                        title = "Reminder: ${task.title}",
                        content = task.description ?: "",
                        dateTime = task.reminderAt,
                        type = ReminderType.TASK,
                        entityId = task.id,
                        channelId = "TASK_REMINDERS"
                    )
                )
            }
        }
        
        // Deadline reminder
        if (task.isDeadlineReminderEnabled && task.dueAt != null) {
            val deadlineTime = DateUtils.parseDateTime(task.dueAt)
            if (deadlineTime.isAfter(now)) {
                notificationScheduler.schedule(
                    Reminder(
                        id = "DEADLINE_${task.id}",
                        title = "Deadline: ${task.title}",
                        content = "Due now",
                        dateTime = task.dueAt,
                        type = ReminderType.DEADLINE,
                        entityId = task.id,
                        channelId = "DEADLINE_REMINDERS"
                    )
                )
            }
        }
    }
}
