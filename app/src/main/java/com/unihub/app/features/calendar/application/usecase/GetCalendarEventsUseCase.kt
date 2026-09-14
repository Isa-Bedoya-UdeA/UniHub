package com.unihub.app.features.calendar.application.usecase

import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCalendarEventsUseCase @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase
) {
    operator fun invoke(userId: String, start: String, end: String): Flow<List<Event>> {
        return getEventsUseCase(userId, start, end)
    }
}
