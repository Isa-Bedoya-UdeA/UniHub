package com.unihub.app.features.events.infrastructure.data.mapper

import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.Location
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.infrastructure.data.local.entity.EventEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.LocationEntity
import com.unihub.app.features.events.infrastructure.data.remote.dto.EventDto
import com.unihub.app.features.events.infrastructure.data.remote.dto.LocationDto

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
        meetingUrl = meetingUrl,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun EventEntity.toDomain(): Event {
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
        meetingUrl = meetingUrl,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
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
        meetingUrl = meetingUrl,
        notes = notes,
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
