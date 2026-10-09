package com.unihub.app.features.events.application.usecase

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventTag
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.model.RecurrenceDay
import com.unihub.app.features.events.domain.model.RecurrenceRule
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.notifications.application.usecase.CancelReminderUseCase
import com.unihub.app.features.notifications.application.usecase.ScheduleEventReminderUseCase
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EventUseCasesTest {

    private val eventsStorage = mutableMapOf<String, Event>()
    private val recurrenceRules = mutableMapOf<String, RecurrenceRule>()
    private val cancelledReminders = mutableListOf<String>()

    private val fakeEventRepository = object : EventRepository {
        override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> =
            flowOf(eventsStorage.values.filter { it.userId == userId })

        override fun getAllEvents(userId: String): Flow<List<Event>> =
            flowOf(eventsStorage.values.filter { it.userId == userId })

        override fun getEventById(id: String): Flow<Event?> =
            flowOf(eventsStorage[id])

        override suspend fun saveEvent(event: Event) {
            eventsStorage[event.id] = event
        }

        override suspend fun updateEvent(event: Event) {
            eventsStorage[event.id] = event
        }

        override suspend fun deleteEvent(id: String) {
            eventsStorage.remove(id)
        }

        override suspend fun syncEvents(userId: String) {}

        override fun getRemindersForEvent(eventId: String): Flow<List<EventReminder>> = flowOf(emptyList())
        override suspend fun saveReminders(eventId: String, reminders: List<EventReminder>) {}
        override suspend fun deleteRemindersForEvent(eventId: String) {}
        override fun getAllEnabledReminders(): Flow<List<EventReminder>> = flowOf(emptyList())

        override fun getRecurrenceRule(ruleId: String): Flow<RecurrenceRule?> = flowOf(recurrenceRules[ruleId])
        override fun getRecurrenceDays(ruleId: String): Flow<List<RecurrenceDay>> = flowOf(emptyList())
        override suspend fun saveRecurrenceRule(rule: RecurrenceRule) { recurrenceRules[rule.id] = rule }
        override suspend fun saveRecurrenceDay(day: RecurrenceDay) {}
        override suspend fun deleteRecurrenceRule(id: String) { recurrenceRules.remove(id) }
        override suspend fun deleteRecurrenceDaysByRule(ruleId: String) {}

        override fun getEventTags(eventId: String): Flow<List<EventTag>> = flowOf(emptyList())
        override suspend fun saveEventTag(eventTag: EventTag) {}
        override suspend fun deleteEventTag(eventId: String, tagId: String) {}
        override suspend fun deleteEventTagsForEvent(eventId: String) {}
    }

    private val fakeNotificationScheduler = object : NotificationScheduler {
        override fun schedule(reminder: Reminder) {}
        override fun cancel(reminderId: String) { cancelledReminders.add(reminderId) }
        override fun cancelAllForEntity(entityId: String) { cancelledReminders.add(entityId) }
        override fun hasExactAlarmPermission(): Boolean = true
    }

    private val scheduleEventReminderUseCase = ScheduleEventReminderUseCase(fakeEventRepository, fakeNotificationScheduler)
    private val cancelReminderUseCase = CancelReminderUseCase(fakeNotificationScheduler)

    private lateinit var getEventsUseCase: GetEventsUseCase
    private lateinit var getEventByIdUseCase: GetEventByIdUseCase
    private lateinit var saveEventUseCase: SaveEventUseCase
    private lateinit var updateEventUseCase: UpdateEventUseCase
    private lateinit var deleteEventUseCase: DeleteEventUseCase
    private lateinit var deleteRecurrenceRuleUseCase: DeleteRecurrenceRuleUseCase

    @Before
    fun setUp() {
        eventsStorage.clear()
        recurrenceRules.clear()
        cancelledReminders.clear()

        getEventsUseCase = GetEventsUseCase(fakeEventRepository)
        getEventByIdUseCase = GetEventByIdUseCase(fakeEventRepository)
        saveEventUseCase = SaveEventUseCase(fakeEventRepository, scheduleEventReminderUseCase)
        updateEventUseCase = UpdateEventUseCase(fakeEventRepository, scheduleEventReminderUseCase)
        deleteEventUseCase = DeleteEventUseCase(fakeEventRepository, cancelReminderUseCase)
        deleteRecurrenceRuleUseCase = DeleteRecurrenceRuleUseCase(fakeEventRepository)
    }

    @Test
    fun `saveEvent adds event to repository`() = runBlocking {
        val event = Event(
            id = "e1",
            userId = "u1",
            academicPeriodId = "p1",
            subjectId = "s1",
            locationId = null,
            recurrenceRuleId = null,
            title = "Clase de Física",
            startAt = "2026-10-10T08:00:00",
            endAt = "2026-10-10T10:00:00",
            locationType = LocationType.PHYSICAL,
            eventType = EventType.CLASS,
            meetingUrl = null,
            notes = "Laboratorio 3",
            reminders = emptyList(),
            recurrenceRule = null,
            recurrenceDays = emptyList(),
            createdAt = "2026-10-09T00:00:00",
            updatedAt = "2026-10-09T00:00:00"
        )

        saveEventUseCase(event)

        assertEquals(1, eventsStorage.size)
        assertEquals("Clase de Física", eventsStorage["e1"]?.title)
        assertEquals(EventType.CLASS, eventsStorage["e1"]?.eventType)
    }

    @Test
    fun `deleteEvent removes event and cancels reminders`() = runBlocking {
        val event = Event(
            id = "e2",
            userId = "u1",
            academicPeriodId = null,
            subjectId = null,
            locationId = null,
            recurrenceRuleId = null,
            title = "Examen Final",
            startAt = "2026-10-20T14:00:00",
            endAt = "2026-10-20T16:00:00",
            locationType = LocationType.NONE,
            eventType = EventType.EXAM,
            meetingUrl = null,
            notes = null,
            reminders = emptyList(),
            recurrenceRule = null,
            recurrenceDays = emptyList(),
            createdAt = "2026-10-09T00:00:00",
            updatedAt = "2026-10-09T00:00:00"
        )
        eventsStorage["e2"] = event

        deleteEventUseCase("e2")

        assertNull(eventsStorage["e2"])
        assertTrue(cancelledReminders.contains("e2"))
    }

    @Test
    fun `deleteRecurrenceRule removes rule from repository`() = runBlocking {
        val rule = RecurrenceRule(
            id = "r1",
            userId = "u1",
            frequency = "WEEKLY",
            interval = 1,
            startDate = "2026-10-09",
            endDate = "2026-12-31",
            createdAt = "2026-10-09T00:00:00",
            updatedAt = "2026-10-09T00:00:00"
        )
        recurrenceRules["r1"] = rule

        deleteRecurrenceRuleUseCase("r1")

        assertNull(recurrenceRules["r1"])
    }
}
