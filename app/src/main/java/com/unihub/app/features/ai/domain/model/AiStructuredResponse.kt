package com.unihub.app.features.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiStructuredResponse(
    val type: AiActionType = AiActionType.CHAT,
    val message: String = "",
    val requiresConfirmation: Boolean = false,
    val missingFields: List<String> = emptyList(),
    val items: List<AiActionItem> = emptyList()
)
