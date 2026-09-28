package com.unihub.app.features.events.infrastructure.data.mapper

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.infrastructure.data.remote.dto.EventDto
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {

    @Test
    fun `Event toEntity and back preserves data`() {
        val event = Event(
            id = "e1",
            userId = "u1",
            academicPeriodId = "p1",
            subjectId = "s1",
            locationId = null,
            recurrenceRuleId = null,
            title = "Clase Calculo",
            startAt = "2025-06-01T08:00:00",
            endAt = "2025-06-01T10:00:00",
            locationType = LocationType.PHYSICAL,
            eventType = EventType.CLASS,
            meetingUrl = null,
            notes = "Traer calculadora",
            reminders = emptyList(),
            recurrenceRule = null,
            recurrenceDays = emptyList(),
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val entity = event.toEntity()
        val restored = entity.toDomain()

        assertEquals(event.id, restored.id)
        assertEquals(event.userId, restored.userId)
        assertEquals(event.title, restored.title)
        assertEquals(event.locationType, restored.locationType)
        assertEquals(event.eventType, restored.eventType)
    }

    @Test
    fun `EventDto toDomain maps fields correctly`() {
        val dto = EventDto(
            id = "e1",
            academicPeriodId = "p1",
            subjectId = "s1",
            title = "Test Event",
            startAt = "2025-06-01T08:00:00",
            endAt = "2025-06-01T10:00:00",
            locationType = "REMOTE",
            meetingUrl = "https://meet.google.com/test",
            notes = null,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val event = dto.toDomain("u1")

        assertEquals("e1", event.id)
        assertEquals("u1", event.userId)
        assertEquals(LocationType.REMOTE, event.locationType)
        assertEquals(EventType.OTHER, event.eventType)
        assertEquals("https://meet.google.com/test", event.meetingUrl)
    }

    @Test
    fun `EventDto toDomain handles invalid locationType gracefully`() {
        val dto = EventDto(
            id = "e1",
            locationType = "INVALID_TYPE",
            title = "Test",
            startAt = "2025-06-01T08:00:00",
            endAt = "2025-06-01T10:00:00",
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val event = dto.toDomain("u1")

        assertEquals(LocationType.NONE, event.locationType)
    }

    @Test
    fun `Event toDto converts location and event types`() {
        val event = Event(
            id = "e1",
            userId = "u1",
            academicPeriodId = null,
            subjectId = null,
            locationId = null,
            recurrenceRuleId = null,
            title = "Test",
            startAt = "2025-06-01T08:00:00",
            endAt = "2025-06-01T10:00:00",
            locationType = LocationType.NONE,
            eventType = EventType.PERSONAL,
            meetingUrl = null,
            notes = null,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val dto = event.toDto()

        assertEquals("2025-06-01T08:00:00", dto.startAt)
        assertEquals("2025-06-01T10:00:00", dto.endAt)
        assertEquals("NONE", dto.locationType)
        assertEquals("PERSONAL", dto.eventType)
    }
}
