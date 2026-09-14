package com.unihub.app.features.notifications.domain.model

enum class ReminderType { EVENT, TASK, DEADLINE }

data class Reminder(
    val id: String,
    val title: String,
    val content: String,
    val dateTime: String, // ISO-8601
    val type: ReminderType,
    val entityId: String,
    val channelId: String
)
