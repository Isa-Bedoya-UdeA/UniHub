package com.unihub.app.features.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiPendingAction(
    val id: String,
    val structuredResponse: AiStructuredResponse
)
