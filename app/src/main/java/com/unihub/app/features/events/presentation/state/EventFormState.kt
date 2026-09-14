package com.unihub.app.features.events.presentation.state

import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.subjects.domain.model.Subject

data class EventFormState(
    val title: String = "",
    val titleError: String? = null,
    val date: String = "",
    val dateError: String? = null,
    val startTime: String = "",
    val startTimeError: String? = null,
    val endTime: String = "",
    val endTimeError: String? = null,
    val locationType: LocationType = LocationType.NONE,
    val eventType: EventType = EventType.PERSONAL,
    val meetingUrl: String = "",
    val meetingUrlError: String? = null,
    val notes: String = "",
    val reminders: List<EventReminder> = emptyList(),
    val subjectId: String? = null,
    val subjects: List<Subject> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
