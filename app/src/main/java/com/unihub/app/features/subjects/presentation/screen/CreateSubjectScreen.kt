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
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubDialog
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
    var showStudyDialog by remember { mutableStateOf(false) }
    var showPeriodDialog by remember { mutableStateOf(false) }

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

    if (showStudyDialog) {
        UniHubDialog(
            onDismissRequest = { showStudyDialog = false }
        ) {
            Column {
                Text(
                    text = "Seleccionar Programa",
                    style = UniHubTheme.typography.h3
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                state.studies.forEach { study ->
                    Text(
                        text = study.name,
                        style = UniHubTheme.typography.body,
                        color = if (state.studyId == study.id) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(SubjectFormEvent.StudySelected(study.id))
                                showStudyDialog = false
                            }
                            .padding(UniHubTheme.spacing.sm)
                    )
                }
            }
        }
    }

    if (showPeriodDialog) {
        UniHubDialog(
            onDismissRequest = { showPeriodDialog = false }
        ) {
            Column {
                Text(
                    text = "Seleccionar Periodo",
                    style = UniHubTheme.typography.h3
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                state.periods.forEach { period ->
                    Text(
                        text = period.name,
                        style = UniHubTheme.typography.body,
                        color = if (state.academicPeriodId == period.id) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(SubjectFormEvent.PeriodSelected(period.id))
                                showPeriodDialog = false
                            }
                            .padding(UniHubTheme.spacing.sm)
                    )
                }
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
            text = "Crear Materia",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

        Text("Programa Académico", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubTextField(
            value = state.studies.find { it.id == state.studyId }?.name ?: "Seleccionar programa",
            onValueChange = {},
            readOnly = true,
            isError = state.studyError != null,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showStudyDialog = true }
        )
        state.studyError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text("Periodo Académico", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubTextField(
            value = state.periods.find { it.id == state.academicPeriodId }?.name ?: "Seleccionar periodo",
            onValueChange = {},
            readOnly = true,
            isError = state.academicPeriodError != null,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPeriodDialog = true }
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
