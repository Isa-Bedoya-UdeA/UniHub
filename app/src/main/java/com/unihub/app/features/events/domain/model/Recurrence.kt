package com.unihub.app.features.events.domain.model

data class RecurrenceRule(
    val id: String,
    val userId: String,
    val frequency: String,
    val interval: Int,
    val startDate: String,
    val endDate: String,
    val createdAt: String,
    val updatedAt: String
)

data class RecurrenceDay(
    val recurrenceRuleId: String,
    val dayOfWeek: Int
)

data class EventTag(
    val eventId: String,
    val tagId: String
)

