package com.unihub.app.features.ai.presentation.state

import com.unihub.app.features.ai.domain.model.AiMessage

data class AiChatUiState(
    val messages: List<AiMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
