package com.unihub.app.features.events.domain.model

enum class LocationType { PHYSICAL, REMOTE, NONE }

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
    val meetingUrl: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)

data class Location(
    val id: String,
    val userId: String,
    val name: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val placeId: String?,
    val createdAt: String,
    val updatedAt: String
)
