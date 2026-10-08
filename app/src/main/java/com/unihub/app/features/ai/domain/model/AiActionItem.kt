package com.unihub.app.features.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiActionItem(
    val name: String? = null,
    val title: String? = null,
    val description: String? = null,
    val code: String? = null,
    val credits: Int? = null,
    val professor: String? = null,
    val color: String? = null,
    val subjectName: String? = null,
    val subjectId: String? = null,
    val studyId: String? = null,
    val academicPeriodId: String? = null,
    val dueAt: String? = null,
    val startAt: String? = null,
    val endAt: String? = null,
    val priority: String? = null,
    val locationType: String? = null,
    val meetingUrl: String? = null,
    val value: Double? = null,
    val weight: Double? = null,
    val notes: String? = null
)
