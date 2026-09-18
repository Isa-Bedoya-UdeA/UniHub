package com.unihub.app.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.application.usecase.ObserveAuthStateUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class EditProfileUiState(
    val user: User? = null,
    val name: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val userId = getCurrentUidUseCase()
            if (userId == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "Usuario no autenticado") }
                return@launch
            }

            userRepository.getUser(userId).collect { user ->
                if (user != null) {
                    _state.update {
                        it.copy(
                            user = user,
                            name = user.name,
                            profileImageUrl = user.profileImageUrl,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun updateName(name: String) {
        _state.update { it.copy(name = name, errorMessage = null) }
    }

    fun updateProfileImageUrl(imageUrl: String?) {
        _state.update { it.copy(profileImageUrl = imageUrl, errorMessage = null) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value
            val user = currentState.user

            if (user == null) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "No se pudo cargar el perfil",
                        type = MessageType.ERROR
                    )
                )
                return@launch
            }

            if (currentState.name.isBlank()) {
                _state.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
                return@launch
            }

            _state.update { it.copy(isSaving = true) }

            try {
                val updatedUser = user.copy(
                    name = currentState.name.trim(),
                    profileImageUrl = currentState.profileImageUrl,
                    updatedAt = Instant.now().toString()
                )

                userRepository.updateUser(updatedUser)

                _state.update { it.copy(isSaving = false) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Perfil actualizado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.message) }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al actualizar el perfil: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }
}
