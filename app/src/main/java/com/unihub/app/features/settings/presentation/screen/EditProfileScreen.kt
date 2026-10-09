package com.unihub.app.features.settings.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.settings.presentation.viewmodel.EditProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditProfileScreen(
    onProfileUpdated: () -> Unit,
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadImage(it) }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                    if (event.type == MessageType.SUCCESS) {
                        onProfileUpdated()
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = UniHubTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(UniHubTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Editar Perfil",
                style = UniHubTheme.typography.h2,
                color = UniHubTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            // Profile Image Section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(UniHubTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!state.profileImageUrl.isNull_blank()) {
                            AsyncImage(
                                model = state.profileImageUrl,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = UniHubTheme.colorScheme.surface,
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(UniHubTheme.spacing.md)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

                    Text(
                        text = "Foto de perfil",
                        style = UniHubTheme.typography.label,
                        color = UniHubTheme.colorScheme.textSecondary
                    )

                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

                    UniHubButton(
                        text = if (state.isUploadingImage) "Subiendo..." else "Cambiar foto",
                        variant = UniHubButtonVariant.Outlined,
                        onClick = { imagePickerLauncher.launch("image/*") },
                        enabled = !state.isUploadingImage && !state.isSaving
                    )
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

            // Name Field
            UniHubTextField(
                value = state.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Nombre",
                placeholder = "Ingresa tu nombre",
                leadingIcon = Icons.Default.Person,
                isError = state.errorMessage != null,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                onImeAction = {
                    if (!state.isSaving && !state.isUploadingImage && state.name.isNotBlank()) {
                        viewModel.saveProfile()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = state.errorMessage ?: "",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

            // Save Button
            UniHubButton(
                text = "Guardar cambios",
                onClick = { viewModel.saveProfile() },
                enabled = !state.isSaving && !state.isUploadingImage && state.name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            // Cancel Button
            UniHubButton(
                text = "Cancelar",
                variant = UniHubButtonVariant.Text,
                onClick = onBack,
                enabled = !state.isSaving && !state.isUploadingImage,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun String?.isNull_blank(): Boolean {
    return this.isNullOrBlank()
}
