package com.unihub.app.features.events.application.usecase

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.notifications.application.usecase.CancelReminderUseCase
import com.unihub.app.features.notifications.application.usecase.ScheduleEventReminderUseCase
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(private val repository: EventRepository) {
    operator fun invoke(userId: String, start: String, end: String) = repository.getEvents(userId, start, end)
}

class GetEventByIdUseCase @Inject constructor(private val repository: EventRepository) {
    operator fun invoke(id: String) = repository.getEventById(id)
}

class SaveEventUseCase @Inject constructor(
    private val repository: EventRepository,
    private val scheduleEventReminderUseCase: ScheduleEventReminderUseCase
) {
    suspend operator fun invoke(event: Event) {
        repository.saveEvent(event)
        repository.saveReminders(event.id, event.reminders)
        scheduleEventReminderUseCase(event)
    }
}

class UpdateEventUseCase @Inject constructor(
    private val repository: EventRepository,
    private val scheduleEventReminderUseCase: ScheduleEventReminderUseCase
) {
    suspend operator fun invoke(event: Event) {
        repository.updateEvent(event)
        repository.saveReminders(event.id, event.reminders)
        scheduleEventReminderUseCase(event)
    }
}

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository,
    private val cancelReminderUseCase: CancelReminderUseCase
) {
    suspend operator fun invoke(id: String) {
        repository.deleteEvent(id)
        cancelReminderUseCase(id)
    }
}

class SaveRecurrenceRuleUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(rule: RecurrenceRule) = repository.saveRecurrenceRule(rule)
}

class SaveRecurrenceDayUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(day: RecurrenceDay) = repository.saveRecurrenceDay(day)
}

class DeleteRecurrenceRuleUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(ruleId: String) {
        repository.deleteRecurrenceDaysByRule(ruleId)
        repository.deleteRecurrenceRule(ruleId)
    }
}
