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
            startAt = 1717200000000L,
            endAt = 1717207200000L,
            locationType = "REMOTE",
            meetingUrl = "https://meet.google.com/test",
            notes = null,
            createdAt = 1717200000000L,
            updatedAt = 1717200000000L
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
            startAt = 0L,
            endAt = 0L,
            createdAt = 0L,
            updatedAt = 0L
        )

        val event = dto.toDomain("u1")

        assertEquals(LocationType.NONE, event.locationType)
    }

    @Test
    fun `Event toDto converts timestamps`() {
        val event = Event(
            id = "e1",
            userId = "u1",
            academicPeriodId = null,
            subjectId = null,
            locationId = null,
            recurrenceRuleId = null,
            title = "Test",
            startAt = "1717200000000",
            endAt = "1717207200000",
            locationType = LocationType.NONE,
            eventType = EventType.PERSONAL,
            meetingUrl = null,
            notes = null,
            createdAt = "1717200000000",
            updatedAt = "1717200000000"
        )

        val dto = event.toDto()

        assertEquals(1717200000000L, dto.startAt)
        assertEquals(1717207200000L, dto.endAt)
        assertEquals("NONE", dto.locationType)
    }
}
