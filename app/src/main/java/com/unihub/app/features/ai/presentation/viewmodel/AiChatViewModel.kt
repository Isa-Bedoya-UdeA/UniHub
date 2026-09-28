package com.unihub.app.features.ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.ai.application.usecase.SendAiMessageUseCase
import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.presentation.state.AiChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val sendAiMessageUseCase: SendAiMessageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatUiState())
    val state: StateFlow<AiChatUiState> = _state.asStateFlow()

    fun onInputChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val currentInput = _state.value.inputText.trim()
        if (currentInput.isBlank() || _state.value.isLoading) return

        val userMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            role = AiMessageRole.USER,
            content = currentInput
        )

        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = sendAiMessageUseCase(currentInput)

            result.fold(
                onSuccess = { response ->
                    val assistantMessage = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = AiMessageRole.ASSISTANT,
                        content = response
                    )
                    _state.update {
                        it.copy(
                            messages = it.messages + assistantMessage,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al enviar el mensaje"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
