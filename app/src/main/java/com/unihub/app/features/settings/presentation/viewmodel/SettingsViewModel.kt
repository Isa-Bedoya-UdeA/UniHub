package com.unihub.app.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
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
import javax.inject.Inject

data class SettingsUiState(
    val userPreferences: UserPreferences? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase
) : ViewModel() {

    private val userId = "current_user"

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadUserPreferences()
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserPreferencesUseCase(userId)
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
                            userPreferences = preferences ?: UserPreferences(userId = userId),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                updateThemeModeUseCase(userId, themeMode)
                _state.update {
                    it.copy(
                        userPreferences = it.userPreferences?.copy(themeMode = themeMode)
                            ?: UserPreferences(userId = userId, themeMode = themeMode)
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
