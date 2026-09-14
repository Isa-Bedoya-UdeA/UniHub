package com.unihub.app.core.common.state

sealed class UiState<out T> {
    data class Loading(val message: String? = null) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

sealed class UiEvent {
    data class ShowMessage(
        val message: String,
        val type: MessageType = MessageType.INFO
    ) : UiEvent()
    
    data class NavigateTo(val route: String) : UiEvent()
    
    object ClearMessage : UiEvent()
}

enum class MessageType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}
