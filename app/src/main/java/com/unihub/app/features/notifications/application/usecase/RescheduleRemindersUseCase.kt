package com.unihub.app.features.notifications.application.usecase

import com.unihub.app.core.util.DateUtils
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.model.ReminderType
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RescheduleRemindersUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository,
    private val notificationScheduler: NotificationScheduler,
    private val getCurrentUidUseCase: GetCurrentUidUseCase
) {
    suspend operator fun invoke() {
        val now = DateUtils.getCurrentTime()
        val userId = getCurrentUidUseCase() ?: return

        val events = eventRepository.getAllEvents(userId).first()
        events.forEach { event ->
            val enabledReminders = event.reminders.filter { it.isEnabled }
            if (enabledReminders.isEmpty()) return@forEach

            val nextOccurrence = if (event.recurrenceRuleId != null) {
                val rule = eventRepository.getRecurrenceRule(event.recurrenceRuleId).first()
                val days = eventRepository.getRecurrenceDays(event.recurrenceRuleId).first()
                if (rule != null) {
                    DateUtils.calculateNextOccurrence(
                        startAt = event.startAt,
                        frequency = rule.frequency,
                        interval = rule.interval,
                        endDate = rule.endDate,
                        recurrenceDays = days.map { it.dayOfWeek }
                    )
                } else {
                    DateUtils.calculateNextOccurrence(event.startAt, "DAILY", 0, "2000-01-01", emptyList())
                }
            } else {
                val startAt = DateUtils.parseDateTime(event.startAt)
                if (startAt.isAfter(now)) startAt else null
            }

            nextOccurrence?.let { occurrence ->
                enabledReminders.forEach { reminder ->
                    scheduleEventReminder(event.id, event.title, reminder, occurrence, now)
                }
            }
        }

        val tasks = taskRepository.getTasks(userId).first()
        tasks.forEach { task ->
            if (task.reminderType != null && task.reminderValue != null && task.dueAt != null) {
                try {
                    val dueDateTime = DateUtils.parseDateTime(task.dueAt)
                    val minutesBefore = when (task.reminderType) {
                        com.unihub.app.features.tasks.domain.model.TaskReminderType.MINUTES_BEFORE -> task.reminderValue.toLong()
                        com.unihub.app.features.tasks.domain.model.TaskReminderType.HOURS_BEFORE -> task.reminderValue.toLong() * 60
                        com.unihub.app.features.tasks.domain.model.TaskReminderType.DAYS_BEFORE -> task.reminderValue.toLong() * 24 * 60
                    }
                    val reminderTime = dueDateTime.minusMinutes(minutesBefore)
                    if (reminderTime.isAfter(now)) {
                        val reminderIso = reminderTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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

    private fun scheduleEventReminder(
        eventId: String,
        eventTitle: String,
        reminder: EventReminder,
        occurrence: LocalDateTime,
        now: LocalDateTime
    ) {
        val minutesBefore = reminder.getTotalMinutesBefore()
        val reminderTime = occurrence.minusMinutes(minutesBefore)
        if (reminderTime.isAfter(now)) {
            notificationScheduler.schedule(
                Reminder(
                    id = "EVENT_${eventId}_${reminder.id}",
                    title = eventTitle,
                    content = "Starts at ${occurrence.format(DateTimeFormatter.ofPattern("HH:mm"))} (${reminder.getDisplayText()})",
                    dateTime = DateUtils.formatDateTime(reminderTime),
                    type = ReminderType.EVENT,
                    entityId = eventId,
                    channelId = "EVENT_REMINDERS"
                )
            )
        }
    }
}
