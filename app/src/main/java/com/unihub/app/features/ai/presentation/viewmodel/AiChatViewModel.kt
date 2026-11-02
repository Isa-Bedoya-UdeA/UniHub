package com.unihub.app.features.ai.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.ai.application.AiChatSessionManager
import com.unihub.app.features.ai.application.usecase.ResolveAndExecuteAiActionUseCase
import com.unihub.app.features.ai.application.usecase.SendAiMessageUseCase
import com.unihub.app.features.ai.domain.model.AiPendingAction
import com.unihub.app.features.ai.infrastructure.data.local.AiChatHistoryDataSource
import com.unihub.app.features.ai.presentation.state.AiChatUiState
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.application.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val sessionManager: AiChatSessionManager,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    val state: StateFlow<AiChatUiState> = sessionManager.state

    init {
        loadUserName()
    }

    // Secondary constructor for existing unit tests
    constructor(
        sendAiMessageUseCase: SendAiMessageUseCase,
        resolveAndExecuteAiActionUseCase: ResolveAndExecuteAiActionUseCase,
        getCurrentUidUseCase: GetCurrentUidUseCase,
        getCurrentUserUseCase: GetCurrentUserUseCase,
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        chatHistoryDataSource: AiChatHistoryDataSource? = null
    ) : this(
        sessionManager = AiChatSessionManager(
            sendAiMessageUseCase = sendAiMessageUseCase,
            resolveAndExecuteAiActionUseCase = resolveAndExecuteAiActionUseCase,
            chatHistoryDataSource = chatHistoryDataSource,
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        ),
        getCurrentUidUseCase = getCurrentUidUseCase,
        getCurrentUserUseCase = getCurrentUserUseCase
    )

    private fun loadUserName() {
        viewModelScope.launch {
            val uid = getCurrentUidUseCase()
            if (uid != null) {
                try {
                    val user = getCurrentUserUseCase(uid).first()
                    val firstName = user?.name?.split(" ")?.firstOrNull() ?: "Usuario"
                    sessionManager.setUserName(firstName)
                } catch (_: Exception) {
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        sessionManager.onInputChanged(text)
    }

    fun sendMessage() {
        sessionManager.sendMessage()
    }

    fun confirmPendingAction(pendingAction: AiPendingAction? = state.value.pendingAction) {
        sessionManager.confirmPendingAction(pendingAction)
    }

    fun cancelPendingAction() {
        sessionManager.cancelPendingAction()
    }

    fun newChat() {
        sessionManager.newChat()
    }

    fun clearError() {
        sessionManager.clearError()
    }
}
