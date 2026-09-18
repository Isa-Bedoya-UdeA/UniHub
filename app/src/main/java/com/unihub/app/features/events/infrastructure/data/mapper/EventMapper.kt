package com.unihub.app.features.events.infrastructure.data.mapper

import com.unihub.app.features.events.domain.model.*
import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.events.infrastructure.data.local.entity.*
import com.unihub.app.features.events.infrastructure.data.remote.dto.EventDto
import com.unihub.app.features.location.infrastructure.data.local.entity.LocationEntity
import com.unihub.app.features.location.infrastructure.data.remote.dto.LocationDto

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        locationId = locationId,
        recurrenceRuleId = recurrenceRuleId,
        title = title,
        startAt = startAt,
        endAt = endAt,
        locationType = locationType,
        eventType = eventType,
        meetingUrl = meetingUrl,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun EventEntity.toDomain(
    reminders: List<EventReminder> = emptyList(),
    recurrenceRule: RecurrenceRule? = null,
    recurrenceDays: List<Int> = emptyList()
): Event {
    return Event(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        locationId = locationId,
        recurrenceRuleId = recurrenceRuleId,
        title = title,
        startAt = startAt,
        endAt = endAt,
        locationType = locationType,
        eventType = eventType,
        meetingUrl = meetingUrl,
        notes = notes,
        reminders = reminders,
        recurrenceRule = recurrenceRule,
        recurrenceDays = recurrenceDays,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RecurrenceRule.toEntity(): RecurrenceRuleEntity {
    return RecurrenceRuleEntity(
        id = id,
        userId = userId,
        frequency = frequency,
        interval = interval,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RecurrenceRuleEntity.toDomain(): RecurrenceRule {
    return RecurrenceRule(
        id = id,
        userId = userId,
        frequency = frequency,
        interval = interval,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RecurrenceDay.toEntity(): RecurrenceDayEntity {
    return RecurrenceDayEntity(
        recurrenceRuleId = recurrenceRuleId,
        dayOfWeek = dayOfWeek
    )
}

fun RecurrenceDayEntity.toDomain(): RecurrenceDay {
    return RecurrenceDay(
        recurrenceRuleId = recurrenceRuleId,
        dayOfWeek = dayOfWeek
    )
}

fun EventTag.toEntity(): EventTagEntity {
    return EventTagEntity(
        eventId = eventId,
        tagId = tagId
    )
}

fun EventTagEntity.toDomain(): EventTag {
    return EventTag(
        eventId = eventId,
        tagId = tagId
    )
}

fun EventDto.toDomain(userId: String): Event {
    return Event(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        locationId = locationId,
        recurrenceRuleId = recurrenceRuleId,
        title = title,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
        locationType = try { LocationType.valueOf(locationType) } catch (e: Exception) { LocationType.NONE },
        eventType = EventType.OTHER,
        meetingUrl = meetingUrl,
        notes = notes,
        reminders = emptyList(),
        recurrenceRule = null,
        recurrenceDays = emptyList(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

fun Event.toDto(): EventDto {
    return EventDto(
        id = id,
        academicPeriodId = academicPeriodId,
        subjectId = subjectId,
        locationId = locationId,
        recurrenceRuleId = recurrenceRuleId,
        title = title,
        startAt = startAt.toLongOrNull() ?: 0L,
        endAt = endAt.toLongOrNull() ?: 0L,
        locationType = locationType.name,
        meetingUrl = meetingUrl,
        notes = notes,
        createdAt = createdAt.toLongOrNull() ?: 0L,
        updatedAt = updatedAt.toLongOrNull() ?: 0L
    )
}

// Location Mappers
fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        userId = userId,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeId = placeId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun LocationEntity.toDomain(): Location {
    return Location(
        id = id,
        userId = userId,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeId = placeId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
