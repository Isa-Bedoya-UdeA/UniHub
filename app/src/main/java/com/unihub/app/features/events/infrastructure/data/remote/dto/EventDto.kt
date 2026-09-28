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
    val startAt: String = "",
    val endAt: String = "",
    val locationType: String = "NONE",
    val eventType: String = "OTHER",
    val meetingUrl: String? = null,
    val notes: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)
