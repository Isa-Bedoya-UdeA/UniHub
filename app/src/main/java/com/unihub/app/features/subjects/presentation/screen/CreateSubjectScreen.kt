package com.unihub.app.features.subjects.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.SelectOption
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubSelect
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.subjects.presentation.viewmodel.SubjectFormEvent
import com.unihub.app.features.subjects.presentation.viewmodel.SubjectFormViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSubjectScreen(
    onSubjectCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: SubjectFormViewModel = hiltViewModel()
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
                        onSubjectCreated()
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isSuccess = state.isSuccess
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isSuccess) UniHubTheme.colorScheme.success else UniHubTheme.colorScheme.error,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            }
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
            text = "Crear Materia",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

        val studyOptions = state.studies.map { SelectOption(it.id, it.name) }
        UniHubSelect(
            options = studyOptions,
            selectedValue = state.studyId,
            onOptionSelected = { viewModel.onEvent(SubjectFormEvent.StudySelected(it)) },
            label = "Programa Académico",
            isError = state.studyError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.studyError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        val periodOptions = state.periods.map { SelectOption(it.id, it.name) }
        UniHubSelect(
            options = periodOptions,
            selectedValue = state.academicPeriodId,
            onOptionSelected = { viewModel.onEvent(SubjectFormEvent.PeriodSelected(it)) },
            label = "Periodo Académico",
            isError = state.academicPeriodError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.academicPeriodError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        if (state.studies.isEmpty()) {
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            Text(
                text = "Debes crear un programa académico primero en Ajustes.",
                style = UniHubTheme.typography.bodySmall,
                color = UniHubTheme.colorScheme.warning
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        UniHubTextField(
            value = state.name,
            onValueChange = { viewModel.onEvent(SubjectFormEvent.EnteredName(it)) },
            label = "Nombre de la materia",
            placeholder = "Ej. Computación Móvil",
            isError = state.nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.nameError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        UniHubTextField(
            value = state.code,
            onValueChange = { viewModel.onEvent(SubjectFormEvent.EnteredCode(it)) },
            label = "Código",
            placeholder = "Ej. COMP-204",
            isError = state.codeError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.codeError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        UniHubTextField(
            value = state.professor,
            onValueChange = { viewModel.onEvent(SubjectFormEvent.EnteredProfessor(it)) },
            label = "Profesor",
            placeholder = "Nombre del docente",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        UniHubTextField(
            value = state.credits,
            onValueChange = { viewModel.onEvent(SubjectFormEvent.EnteredCredits(it)) },
            label = "Créditos",
            placeholder = "Ej. 4",
            isError = state.creditsError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.creditsError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        UniHubButton(
            text = "Guardar Materia",
            enabled = state.studies.isNotEmpty() && state.periods.isNotEmpty() && !state.isLoading,
            onClick = { viewModel.onEvent(SubjectFormEvent.SaveSubject) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    }
}
