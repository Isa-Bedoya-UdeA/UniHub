package com.unihub.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.auth.application.usecase.GetCurrentUserUseCase
import com.unihub.app.features.auth.application.usecase.ObserveAuthStateUseCase
import com.unihub.app.features.auth.application.usecase.SignInWithGoogleUseCase
import com.unihub.app.features.auth.application.usecase.SignOutUseCase
import com.unihub.app.features.auth.application.usecase.UpdateUserUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.presentation.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collectLatest { authState ->
                _state.update { it.copy(authState = authState) }
                if (authState is AuthState.Authenticated) {
                    loadUser(authState.uid)
                }
            }
        }
    }

    private fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCurrentUserUseCase(userId).collectLatest { user ->
                _state.update { it.copy(user = user, isLoading = false) }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            signInWithGoogleUseCase(idToken)
                .onSuccess { uid ->
                    _state.update {
                        it.copy(
                            authState = AuthState.Authenticated(uid),
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            authState = AuthState.Error(exception.message ?: "Error de autenticación"),
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            signOutUseCase()
            _state.update {
                it.copy(
                    authState = AuthState.Unauthenticated,
                    user = null,
                    isLoading = false
                )
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            updateUserUseCase(user)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
