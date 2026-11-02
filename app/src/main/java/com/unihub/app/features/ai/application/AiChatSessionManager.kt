package com.unihub.app.features.ai.application

import com.unihub.app.features.ai.application.usecase.ResolveAndExecuteAiActionUseCase
import com.unihub.app.features.ai.application.usecase.SendAiMessageUseCase
import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.domain.model.AiPendingAction
import com.unihub.app.features.ai.domain.model.AiResponseSource
import com.unihub.app.features.ai.domain.repository.AiConversationMessage
import com.unihub.app.features.ai.infrastructure.data.local.AiChatHistoryDataSource
import com.unihub.app.features.ai.presentation.state.AiChatStatus
import com.unihub.app.features.ai.presentation.state.AiChatUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatSessionManager(
    private val sendAiMessageUseCase: SendAiMessageUseCase,
    private val resolveAndExecuteAiActionUseCase: ResolveAndExecuteAiActionUseCase,
    private val chatHistoryDataSource: AiChatHistoryDataSource? = null,
    coroutineScope: CoroutineScope? = null
) {
    internal var scope: CoroutineScope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    constructor(
        sendAiMessageUseCase: SendAiMessageUseCase,
        resolveAndExecuteAiActionUseCase: ResolveAndExecuteAiActionUseCase,
        chatHistoryDataSource: AiChatHistoryDataSource
    ) : this(sendAiMessageUseCase, resolveAndExecuteAiActionUseCase, chatHistoryDataSource as AiChatHistoryDataSource?, null)

    constructor(
        sendAiMessageUseCase: SendAiMessageUseCase,
        resolveAndExecuteAiActionUseCase: ResolveAndExecuteAiActionUseCase
    ) : this(sendAiMessageUseCase, resolveAndExecuteAiActionUseCase, null, null)

    private val _state = MutableStateFlow(AiChatUiState())
    val state: StateFlow<AiChatUiState> = _state.asStateFlow()

    init {
        val persistentMessages = chatHistoryDataSource?.loadMessages() ?: emptyList()
        if (persistentMessages.isNotEmpty()) {
            _state.update { it.copy(messages = persistentMessages) }
        }
    }

    fun setUserName(name: String) {
        _state.update { it.copy(userName = name) }
    }

    fun onInputChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun newChat() {
        chatHistoryDataSource?.clearMessages()
        _state.update {
            AiChatUiState(userName = it.userName)
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun sendMessage() {
        val currentInput = _state.value.inputText.trim()
        if (currentInput.isBlank() || _state.value.isLoading) return

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
        chatHistoryDataSource?.saveMessages(updatedMessages)

        scope.launch {
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
                    chatHistoryDataSource?.saveMessages(messagesWithResponse)
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
                    chatHistoryDataSource?.saveMessages(messagesWithError)
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

        scope.launch {
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
                    chatHistoryDataSource?.saveMessages(updatedMessages)
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
                    chatHistoryDataSource?.saveMessages(updatedMessages)
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
        chatHistoryDataSource?.saveMessages(updatedMessages)
    }
}
