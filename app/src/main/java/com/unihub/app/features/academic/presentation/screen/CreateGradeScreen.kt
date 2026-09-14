package com.unihub.app.features.academic.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.presentation.viewmodel.GradeFormEvent
import com.unihub.app.features.academic.presentation.viewmodel.GradeFormViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateGradeScreen(
    subjectId: String,
    gradeId: String? = null,
    onGradeCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: GradeFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                    if (event.type == MessageType.SUCCESS) {
                        onGradeCreated()
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
                text = if (gradeId == null) "Registrar Nota" else "Editar Nota",
                style = UniHubTheme.typography.h2,
                color = UniHubTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

            UniHubTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(GradeFormEvent.EnteredName(it)) },
                label = "Nombre de la evaluación",
                placeholder = "Ej. Parcial 1",
                isError = state.nameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            state.nameError?.let {
                Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            UniHubTextField(
                value = state.value,
                onValueChange = { viewModel.onEvent(GradeFormEvent.EnteredValue(it)) },
                label = "Nota",
                placeholder = "0.0 - 5.0",
                isError = state.valueError != null,
                modifier = Modifier.fillMaxWidth()
            )
            state.valueError?.let {
                Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            UniHubTextField(
                value = state.weight,
                onValueChange = { viewModel.onEvent(GradeFormEvent.EnteredWeight(it)) },
                label = "Peso (%)",
                placeholder = "Ej. 20",
                isError = state.weightError != null,
                modifier = Modifier.fillMaxWidth()
            )
            state.weightError?.let {
                Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            UniHubButton(
                text = if (gradeId == null) "Guardar Nota" else "Guardar Cambios",
                onClick = { viewModel.onEvent(GradeFormEvent.SaveGrade) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
        }
    }
}
