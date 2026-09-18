package com.unihub.app.features.auth.domain.model

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val uid: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
