package com.unihub.app.features.events.application.usecase

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.repository.EventRepository
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(private val repository: EventRepository) {
    operator fun invoke(userId: String, start: String, end: String) = repository.getEvents(userId, start, end)
}

class GetEventByIdUseCase @Inject constructor(private val repository: EventRepository) {
    operator fun invoke(id: String) = repository.getEventById(id)
}

class SaveEventUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(event: Event) = repository.saveEvent(event)
}

class UpdateEventUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(event: Event) = repository.updateEvent(event)
}

class DeleteEventUseCase @Inject constructor(private val repository: EventRepository) {
    suspend operator fun invoke(id: String) = repository.deleteEvent(id)
}
