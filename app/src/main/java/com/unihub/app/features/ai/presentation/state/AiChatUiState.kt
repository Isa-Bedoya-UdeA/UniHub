package com.unihub.app.features.ai.presentation.state

import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiPendingAction

enum class AiChatStatus {
    IDLE,
    SENDING,
    AI_RESPONSE,
    DETERMINISTIC_FALLBACK,
    LIMITED_MODE,
    CONFIRMATION_REQUIRED,
    ACTION_EXECUTING,
    ERROR
}

data class AiChatUiState(
    val messages: List<AiMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val status: AiChatStatus = AiChatStatus.IDLE,
    val errorMessage: String? = null,
    val userName: String = "Usuario",
    val isLimitedMode: Boolean = false,
    val pendingAction: AiPendingAction? = null
)
