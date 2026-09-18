package com.unihub.app.features.auth.presentation.state

import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
