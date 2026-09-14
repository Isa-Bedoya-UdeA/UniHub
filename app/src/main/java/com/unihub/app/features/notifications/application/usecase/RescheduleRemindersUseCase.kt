package com.unihub.app.features.notifications.application.usecase

import com.unihub.app.core.util.DateUtils
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.model.ReminderType
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RescheduleRemindersUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke() {
        val now = DateUtils.getCurrentTime()

        val events = eventRepository.getAllEvents("current_user").first()
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

        val tasks = taskRepository.getTasks("current_user").first()
        tasks.forEach { task ->
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
