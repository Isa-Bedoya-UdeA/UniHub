package com.unihub.app.features.ai.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.ai.application.usecase.ResolveAndExecuteAiActionUseCase
import com.unihub.app.features.ai.application.usecase.SendAiMessageUseCase
import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.domain.model.AiPendingAction
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiConversationMessage
import com.unihub.app.features.ai.presentation.state.AiChatStatus
import com.unihub.app.features.ai.presentation.state.AiChatUiState
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.application.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val sendAiMessageUseCase: SendAiMessageUseCase,
    private val resolveAndExecuteAiActionUseCase: ResolveAndExecuteAiActionUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val chatHistoryDataSource: com.unihub.app.features.ai.infrastructure.data.local.AiChatHistoryDataSource? = null
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }

    private val _state = MutableStateFlow(AiChatUiState())
    val state: StateFlow<AiChatUiState> = _state.asStateFlow()

    init {
        restoreMessages()
        loadUserName()
    }

    private fun restoreMessages() {
        val persistentMessages = chatHistoryDataSource?.loadMessages()
        if (!persistentMessages.isNullOrEmpty()) {
            _state.update { it.copy(messages = persistentMessages) }
            return
        }

        val savedMessagesJson = savedStateHandle.get<String>(KEY_MESSAGES)
        if (savedMessagesJson != null) {
            try {
                val messages = json.decodeFromString<List<AiMessage>>(savedMessagesJson)
                _state.update { it.copy(messages = messages) }
            } catch (_: Exception) {
            }
        }
    }

    private fun saveMessages(messages: List<AiMessage>) {
        chatHistoryDataSource?.saveMessages(messages)
        try {
            val messagesJson = json.encodeToString(messages)
            savedStateHandle[KEY_MESSAGES] = messagesJson
        } catch (_: Exception) {
        }
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val uid = getCurrentUidUseCase()
            if (uid != null) {
                val user = getCurrentUserUseCase(uid).first()
                val firstName = user?.name?.split(" ")?.firstOrNull() ?: "Usuario"
                _state.update { it.copy(userName = firstName) }
            }
        }
    }

    fun onInputChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val currentInput = _state.value.inputText.trim()
        if (currentInput.isBlank() || _state.value.isLoading) return

        val pendingAction = _state.value.pendingAction

        if (pendingAction != null) {
            handleClarificationFollowUp(currentInput, pendingAction)
            return
        }

        val userMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            role = AiMessageRole.USER,
            content = currentInput
        )

        val updatedMessages = _state.value.messages + userMessage
        _state.update {
            it.copy(
                messages = updatedMessages,
                inputText = "",
                isLoading = true,
                status = AiChatStatus.SENDING,
                errorMessage = null
            )
        }
        saveMessages(updatedMessages)

        viewModelScope.launch {
            val conversationHistory = buildConversationHistory(updatedMessages)
            val result = sendAiMessageUseCase(currentInput, conversationHistory)

            result.fold(
                onSuccess = { response ->
                    val isLimited = response.source == AiResponseSource.LIMITED_MODE
                    val role = if (isLimited) AiMessageRole.SYSTEM else AiMessageRole.ASSISTANT
                    val structured = response.structuredResponse

                    val newPendingAction = if (structured != null && structured.requiresConfirmation && structured.items.isNotEmpty()) {
                        AiPendingAction(
                            id = UUID.randomUUID().toString(),
                            structuredResponse = structured
                        )
                    } else null

                    val assistantMessage = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = role,
                        content = response.text,
                        source = response.source,
                        structuredResponse = structured,
                        pendingAction = newPendingAction
                    )

                    val messagesWithResponse = _state.value.messages + assistantMessage
                    val newStatus = when {
                        newPendingAction != null -> AiChatStatus.CONFIRMATION_REQUIRED
                        response.source == AiResponseSource.AI -> AiChatStatus.AI_RESPONSE
                        response.source == AiResponseSource.LOCAL_FALLBACK -> AiChatStatus.DETERMINISTIC_FALLBACK
                        else -> AiChatStatus.LIMITED_MODE
                    }

                    _state.update {
                        it.copy(
                            messages = messagesWithResponse,
                            isLoading = false,
                            status = newStatus,
                            isLimitedMode = isLimited,
                            pendingAction = newPendingAction
                        )
                    }
                    saveMessages(messagesWithResponse)
                },
                onFailure = { error ->
                    val errorMessage = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = AiMessageRole.ERROR,
                        content = error.message ?: "Error al procesar la solicitud"
                    )
                    val messagesWithError = _state.value.messages + errorMessage
                    _state.update {
                        it.copy(
                            messages = messagesWithError,
                            isLoading = false,
                            status = AiChatStatus.ERROR,
                            errorMessage = error.message ?: "Error al procesar la solicitud"
                        )
                    }
                    saveMessages(messagesWithError)
                }
            )
        }
    }

    private fun handleClarificationFollowUp(answer: String, pendingAction: AiPendingAction) {
        val userMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            role = AiMessageRole.USER,
            content = answer
        )

        val updatedMessages = _state.value.messages + userMessage
        _state.update {
            it.copy(
                messages = updatedMessages,
                inputText = "",
                isLoading = true,
                status = AiChatStatus.SENDING,
                errorMessage = null
            )
        }
        saveMessages(updatedMessages)

        viewModelScope.launch {
            val conversationHistory = buildConversationHistory(updatedMessages)
            val contextualMessage = "Respuesta del usuario a la aclaración: \"$answer\".\nSolicitud original pendiente de ejecutar: ${pendingAction.structuredResponse.message}"
            val result = sendAiMessageUseCase(contextualMessage, conversationHistory)

            result.fold(
                onSuccess = { response ->
                    val isLimited = response.source == AiResponseSource.LIMITED_MODE
                    val role = if (isLimited) AiMessageRole.SYSTEM else AiMessageRole.ASSISTANT
                    val structured = response.structuredResponse

                    val newPendingAction = if (structured != null && structured.requiresConfirmation && structured.items.isNotEmpty()) {
                        AiPendingAction(
                            id = UUID.randomUUID().toString(),
                            structuredResponse = structured
                        )
                    } else null

                    val assistantMessage = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = role,
                        content = response.text,
                        source = response.source,
                        structuredResponse = structured,
                        pendingAction = newPendingAction
                    )

                    val messagesWithResponse = _state.value.messages + assistantMessage
                    val newStatus = when {
                        newPendingAction != null -> AiChatStatus.CONFIRMATION_REQUIRED
                        response.source == AiResponseSource.AI -> AiChatStatus.AI_RESPONSE
                        response.source == AiResponseSource.LOCAL_FALLBACK -> AiChatStatus.DETERMINISTIC_FALLBACK
                        else -> AiChatStatus.LIMITED_MODE
                    }

                    _state.update {
                        it.copy(
                            messages = messagesWithResponse,
                            isLoading = false,
                            status = newStatus,
                            isLimitedMode = isLimited,
                            pendingAction = newPendingAction
                        )
                    }
                    saveMessages(messagesWithResponse)
                },
                onFailure = { error ->
                    val errorMessage = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = AiMessageRole.ERROR,
                        content = error.message ?: "Error al procesar la solicitud"
                    )
                    val messagesWithError = _state.value.messages + errorMessage
                    _state.update {
                        it.copy(
                            messages = messagesWithError,
                            isLoading = false,
                            status = AiChatStatus.ERROR,
                            errorMessage = error.message ?: "Error al procesar la solicitud",
                            pendingAction = null
                        )
                    }
                    saveMessages(messagesWithError)
                }
            )
        }
    }

    private fun buildConversationHistory(messages: List<AiMessage>): List<AiConversationMessage> {
        return messages
            .filter { it.role == AiMessageRole.USER || it.role == AiMessageRole.ASSISTANT }
            .takeLast(SendAiMessageUseCase.MAX_CONVERSATION_MESSAGES)
            .map { msg ->
                AiConversationMessage(
                    role = if (msg.role == AiMessageRole.USER) "user" else "assistant",
                    content = msg.content
                )
            }
    }

    fun confirmPendingAction(pendingAction: AiPendingAction? = state.value.pendingAction) {
        val actionToExecute = pendingAction ?: state.value.pendingAction ?: return

        _state.update {
            it.copy(
                isLoading = true,
                status = AiChatStatus.ACTION_EXECUTING
            )
        }

        viewModelScope.launch {
            val result = resolveAndExecuteAiActionUseCase.execute(actionToExecute)

            result.fold(
                onSuccess = { successMsg ->
                    val confirmationMsg = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = AiMessageRole.SYSTEM,
                        content = successMsg
                    )
                    val updatedMessages = _state.value.messages + confirmationMsg
                    _state.update {
                        it.copy(
                            messages = updatedMessages,
                            isLoading = false,
                            status = AiChatStatus.AI_RESPONSE,
                            pendingAction = null
                        )
                    }
                    saveMessages(updatedMessages)
                },
                onFailure = { error ->
                    val errorMsg = AiMessage(
                        id = UUID.randomUUID().toString(),
                        role = AiMessageRole.ERROR,
                        content = "Error al ejecutar la acción: ${error.message}"
                    )
                    val updatedMessages = _state.value.messages + errorMsg
                    _state.update {
                        it.copy(
                            messages = updatedMessages,
                            isLoading = false,
                            status = AiChatStatus.ERROR,
                            pendingAction = null,
                            errorMessage = error.message
                        )
                    }
                    saveMessages(updatedMessages)
                }
            )
        }
    }

    fun cancelPendingAction() {
        val cancelMsg = AiMessage(
            id = UUID.randomUUID().toString(),
            role = AiMessageRole.SYSTEM,
            content = "Operación cancelada por el usuario."
        )
        val updatedMessages = _state.value.messages + cancelMsg
        _state.update {
            it.copy(
                messages = updatedMessages,
                pendingAction = null,
                status = AiChatStatus.IDLE
            )
        }
        saveMessages(updatedMessages)
    }

    fun newChat() {
        chatHistoryDataSource?.clearMessages()
        savedStateHandle.remove<String>(KEY_MESSAGES)
        _state.update {
            it.copy(
                messages = emptyList(),
                inputText = "",
                errorMessage = null,
                status = AiChatStatus.IDLE,
                isLimitedMode = false,
                pendingAction = null
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    companion object {
        private const val KEY_MESSAGES = "ai_chat_messages"
    }
}
