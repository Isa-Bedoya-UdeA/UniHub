package com.unihub.app.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.settings.application.usecase.GetUserPreferencesUseCase
import com.unihub.app.features.settings.application.usecase.UpdateThemeModeUseCase
import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.domain.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

import com.unihub.app.features.auth.application.usecase.ObserveAuthStateUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.settings.domain.repository.SettingsRepository

data class SettingsUiState(
    val user: User? = null,
    val userPreferences: UserPreferences? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private var currentUserId = getCurrentUidUseCase() ?: "current_user"

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        if (currentUserId != "current_user") {
            loadUserPreferences(currentUserId)
        }
        loadCurrentUser()
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { authState ->
                if (authState is AuthState.Authenticated) {
                    val uid = authState.uid
                    if (uid != currentUserId || _state.value.userPreferences == null) {
                        currentUserId = uid
                        loadUserPreferences(uid)
                        loadCurrentUser()
                    }
                }
            }
        }
    }

    private fun loadUserPreferences(uid: String) {
        if (uid.isBlank() || uid == "current_user") return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            launch {
                try {
                    settingsRepository.syncPreferences(uid)
                } catch (e: Exception) {
                    android.util.Log.e("SettingsViewModel", "syncPreferences failed: ${e.message}")
                }
            }
            getUserPreferencesUseCase(uid)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Error al cargar preferencias"
                        )
                    }
                    _uiEvent.emit(
                        UiEvent.ShowMessage(
                            message = "Error al cargar preferencias: ${e.message ?: "Error desconocido"}",
                            type = MessageType.ERROR
                        )
                    )
                }
                .collect { preferences ->
                    _state.update {
                        it.copy(
                            userPreferences = preferences ?: UserPreferences(userId = uid),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val uid = getCurrentUidUseCase() ?: return@launch
            android.util.Log.d("SettingsViewModel", "=== LOAD CURRENT USER ===")
            android.util.Log.d("SettingsViewModel", "UID: $uid")
            
            if (uid != "current_user") {
                userRepository.syncProfile(uid)
            }

            userRepository.getUser(uid).collect { user ->
                android.util.Log.d("SettingsViewModel", "User received from repository:")
                android.util.Log.d("SettingsViewModel", "  User ID: ${user?.userId}")
                android.util.Log.d("SettingsViewModel", "  Name: ${user?.name}")
                android.util.Log.d("SettingsViewModel", "  Profile Image URL: ${user?.profileImageUrl}")
                
                if (user != null) {
                    _state.update { it.copy(user = user) }
                } else {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    if (firebaseUser != null && firebaseUser.uid == uid) {
                        val now = Instant.now().toString()
                        val fallbackUser = User(
                            userId = uid,
                            name = firebaseUser.displayName?.ifBlank { null } ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            profileImageUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = now,
                            updatedAt = now
                        )
                        _state.update { it.copy(user = fallbackUser) }
                        userRepository.saveUser(fallbackUser)
                    }
                }
            }
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                updateThemeModeUseCase(currentUserId, themeMode)
                _state.update {
                    it.copy(
                        userPreferences = it.userPreferences?.copy(themeMode = themeMode)
                            ?: UserPreferences(userId = currentUserId, themeMode = themeMode)
                    )
                }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Tema actualizado exitosamente",
                        type = MessageType.SUCCESS
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        message = "Error al actualizar el tema: ${e.message ?: "Error desconocido"}",
                        type = MessageType.ERROR
                    )
                )
            }
        }
    }
}
