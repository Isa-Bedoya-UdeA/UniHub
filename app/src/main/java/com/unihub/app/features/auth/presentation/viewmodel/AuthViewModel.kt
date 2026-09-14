package com.unihub.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.auth.application.usecase.GetCurrentUserUseCase
import com.unihub.app.features.auth.application.usecase.UpdateUserUseCase
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.presentation.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getCurrentUserUseCase(userId).collectLatest { user ->
                _state.value = _state.value.copy(user = user, isLoading = false)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            updateUserUseCase(user)
        }
    }
}
