package com.unihub.app.features.events.infrastructure.repository

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor() : EventRepository {

    private val today = LocalDate.now(ZoneId.of("America/Bogota"))

    private val mockEvents = MutableStateFlow<List<Event>>(
        listOf(
            Event(
                id = "1", userId = "user123", academicPeriodId = "2026-2",
                subjectId = "1", locationId = null, recurrenceRuleId = null,
                title = "Clase de Computación Móvil", startAt = "${today}T08:00",
                endAt = "${today}T10:00", locationType = LocationType.PHYSICAL,
                meetingUrl = null, notes = "Bloque 19", createdAt = "", updatedAt = ""
            ),
            Event(
                id = "2", userId = "user123", academicPeriodId = "2026-2",
                subjectId = "2", locationId = null, recurrenceRuleId = null,
                title = "Laboratorio Bases de Datos", startAt = "${today}T10:00",
                endAt = "${today}T12:00", locationType = LocationType.PHYSICAL,
                meetingUrl = null, notes = "Bloque 18", createdAt = "", updatedAt = ""
            )
        )
    )

    override fun getEvents(userId: String, start: String, end: String): Flow<List<Event>> =
        mockEvents.map { it.filter { e -> e.userId == userId } }

    override fun getEventById(id: String): Flow<Event?> = mockEvents.map { it.find { e -> e.id == id } }

    override suspend fun saveEvent(event: Event) {
        val current = mockEvents.value.toMutableList()
        val index = current.indexOfFirst { it.id == event.id }
        if (index != -1) current[index] = event else current.add(event)
        mockEvents.emit(current)
    }

    override suspend fun updateEvent(event: Event) {
        saveEvent(event)
    }

    override suspend fun deleteEvent(id: String) {
        mockEvents.emit(mockEvents.value.filter { it.id != id })
    }

    override suspend fun syncEvents(userId: String) {}
}
