package com.unihub.app.features.auth.presentation.state

import com.unihub.app.features.auth.domain.model.User

data class AuthUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
