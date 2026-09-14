package com.unihub.app.features.events.infrastructure.data.mapper

import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.infrastructure.data.local.entity.EventReminderEntity

fun EventReminder.toEntity(): EventReminderEntity {
    return EventReminderEntity(
        id = id,
        eventId = eventId,
        reminderType = reminderType,
        value = value,
        isEnabled = isEnabled
    )
}

fun EventReminderEntity.toDomain(): EventReminder {
    return EventReminder(
        id = id,
        eventId = eventId,
        reminderType = reminderType,
        value = value,
        isEnabled = isEnabled
    )
}
