package com.unihub.app.features.subjects.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.subjects.presentation.viewmodel.SubjectFormEvent
import com.unihub.app.features.subjects.presentation.viewmodel.SubjectFormViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditSubjectScreen(
    subjectId: String,
    onSubjectUpdated: () -> Unit,
    onBack: () -> Unit,
    viewModel: SubjectFormViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(key1 = subjectId) {
        viewModel.onEvent(SubjectFormEvent.LoadSubject(subjectId))
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SubjectFormViewModel.UiEvent.SaveSuccess -> {
                    onSubjectUpdated()
                }
                is SubjectFormViewModel.UiEvent.ShowSnackbar -> {
                    // Handle snackbar
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.md)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Editar Materia",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

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
            modifier = Modifier.fillMaxWidth()
        )
        
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
            text = "Guardar Cambios",
            onClick = { viewModel.onEvent(SubjectFormEvent.SaveSubject) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
