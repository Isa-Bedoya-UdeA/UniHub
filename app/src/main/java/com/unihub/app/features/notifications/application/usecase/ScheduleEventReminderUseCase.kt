package com.unihub.app.features.notifications.application.usecase

import com.unihub.app.core.util.DateUtils
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.model.ReminderType
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ScheduleEventReminderUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(event: Event) {
        notificationScheduler.cancelAllForEntity(event.id)
        
        val enabledReminders = event.reminders.filter { it.isEnabled }
        if (enabledReminders.isEmpty()) return

        val now = DateUtils.getCurrentTime()
        
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
            } else null
        } else {
            val startAt = DateUtils.parseDateTime(event.startAt)
            if (startAt.isAfter(now)) startAt else null
        }

        nextOccurrence?.let { occurrence ->
            enabledReminders.forEach { reminder ->
                scheduleReminder(event, reminder, occurrence, now)
            }
        }
    }

    private fun scheduleReminder(
        event: Event,
        reminder: EventReminder,
        occurrence: LocalDateTime,
        now: LocalDateTime
    ) {
        val minutesBefore = reminder.getTotalMinutesBefore()
        val reminderTime = occurrence.minusMinutes(minutesBefore)
        
        if (reminderTime.isAfter(now)) {
            val formattedTime = occurrence.format(DateTimeFormatter.ofPattern("HH:mm"))
            val displayText = reminder.getDisplayText()
            val reminderNotification = Reminder(
                id = "EVENT_${event.id}_${reminder.id}",
                title = event.title,
                content = "Starts at $formattedTime ($displayText)",
                dateTime = DateUtils.formatDateTime(reminderTime),
                type = ReminderType.EVENT,
                entityId = event.id,
                channelId = "EVENT_REMINDERS"
            )
            notificationScheduler.schedule(reminderNotification)
        }
    }
}
