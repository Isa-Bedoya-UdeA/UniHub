package com.unihub.app.features.events.domain.model

enum class ReminderType {
    MINUTES_BEFORE,
    HOURS_BEFORE,
    DAYS_BEFORE
}

data class EventReminder(
    val id: String,
    val eventId: String,
    val reminderType: ReminderType,
    val value: Int,
    val isEnabled: Boolean = true
) {
    fun getTotalMinutesBefore(): Long {
        return when (reminderType) {
            ReminderType.MINUTES_BEFORE -> value.toLong()
            ReminderType.HOURS_BEFORE -> value.toLong() * 60
            ReminderType.DAYS_BEFORE -> value.toLong() * 24 * 60
        }
    }

    fun getDisplayText(): String {
        return when (reminderType) {
            ReminderType.MINUTES_BEFORE -> "$value min antes"
            ReminderType.HOURS_BEFORE -> "$value h antes"
            ReminderType.DAYS_BEFORE -> "$value d\u00eda${if (value != 1) "s" else ""} antes"
        }
    }
}
