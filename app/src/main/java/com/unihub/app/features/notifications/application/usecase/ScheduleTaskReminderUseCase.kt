package com.unihub.app.features.notifications.application.usecase

import com.unihub.app.core.util.DateUtils
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.model.ReminderType
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskReminderType
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

        if (task.reminderType != null && task.reminderValue != null && task.dueAt != null) {
            try {
                val dueDateTime = DateUtils.parseDateTime(task.dueAt)
                val minutesBefore = when (task.reminderType) {
                    TaskReminderType.MINUTES_BEFORE -> task.reminderValue.toLong()
                    TaskReminderType.HOURS_BEFORE -> task.reminderValue.toLong() * 60
                    TaskReminderType.DAYS_BEFORE -> task.reminderValue.toLong() * 24 * 60
                }
                val reminderTime = dueDateTime.minusMinutes(minutesBefore)
                if (reminderTime.isAfter(now)) {
                    val reminderIso = reminderTime.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    notificationScheduler.schedule(
                        Reminder(
                            id = "TASK_${task.id}",
                            title = "Recordatorio: ${task.title}",
                            content = task.description ?: "",
                            dateTime = reminderIso,
                            type = ReminderType.TASK,
                            entityId = task.id,
                            channelId = "TASK_REMINDERS"
                        )
                    )
                }
            } catch (_: Exception) { }
        }
        
        if (task.isDeadlineReminderEnabled && task.dueAt != null) {
            try {
                val deadlineTime = DateUtils.parseDateTime(task.dueAt)
                if (deadlineTime.isAfter(now)) {
                    notificationScheduler.schedule(
                        Reminder(
                            id = "DEADLINE_${task.id}",
                            title = "Vencimiento: ${task.title}",
                            content = "La tarea vence ahora",
                            dateTime = task.dueAt,
                            type = ReminderType.DEADLINE,
                            entityId = task.id,
                            channelId = "DEADLINE_REMINDERS"
                        )
                    )
                }
            } catch (_: Exception) { }
        }
    }
}
