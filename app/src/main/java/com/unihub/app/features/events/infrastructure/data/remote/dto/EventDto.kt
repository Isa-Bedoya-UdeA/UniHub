package com.unihub.app.features.events.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String = "",
    val academicPeriodId: String? = null,
    val subjectId: String? = null,
    val locationId: String? = null,
    val recurrenceRuleId: String? = null,
    val title: String = "",
    val startAt: Long = 0L,
    val endAt: Long = 0L,
    val locationType: String = "NONE",
    val meetingUrl: String? = null,
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class LocationDto(
    val id: String = "",
    val name: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val placeId: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
