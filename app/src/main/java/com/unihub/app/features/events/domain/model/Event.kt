package com.unihub.app.features.events.domain.model

enum class LocationType { PHYSICAL, REMOTE, NONE }

enum class EventType {
    CLASS, EXAM, MEETING, PERSONAL, OTHER;

    fun displayName(): String = when (this) {
        CLASS -> "Clase"
        EXAM -> "Examen"
        MEETING -> "Reunión"
        PERSONAL -> "Personal"
        OTHER -> "Otro"
    }
}

data class Event(
    val id: String,
    val userId: String,
    val academicPeriodId: String?,
    val subjectId: String?,
    val locationId: String?,
    val recurrenceRuleId: String?,
    val title: String,
    val startAt: String,
    val endAt: String,
    val locationType: LocationType,
    val eventType: EventType,
    val meetingUrl: String?,
    val notes: String?,
    val reminders: List<EventReminder> = emptyList(),
    val recurrenceRule: RecurrenceRule? = null,
    val recurrenceDays: List<Int> = emptyList(),
    val createdAt: String,
    val updatedAt: String
) {
    val isReminderEnabled: Boolean
        get() = reminders.any { it.isEnabled }

    val reminderMinutesBefore: Int?
        get() = reminders
            .filter { it.isEnabled }
            .minOfOrNull { it.getTotalMinutesBefore().toInt() }
}
