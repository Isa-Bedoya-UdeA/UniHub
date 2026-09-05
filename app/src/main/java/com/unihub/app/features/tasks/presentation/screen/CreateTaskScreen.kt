package com.unihub.app.features.tasks.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.tasks.presentation.viewmodel.TaskFormEvent
import com.unihub.app.features.tasks.presentation.viewmodel.TaskFormViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    subjectId: String? = null,
    taskId: String? = null,
    onTaskCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: TaskFormViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = subjectId, key2 = taskId) {
        viewModel.onEvent(TaskFormEvent.Init(subjectId, taskId))
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TaskFormViewModel.UiEvent.SaveSuccess -> {
                    onTaskCreated()
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        val formatted = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        viewModel.onEvent(TaskFormEvent.EnteredDueDate(formatted))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.md)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (taskId == null) "Crear Tarea" else "Editar Tarea",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

        UniHubTextField(
            value = state.title,
            onValueChange = { viewModel.onEvent(TaskFormEvent.EnteredTitle(it)) },
            label = "Título de la tarea",
            placeholder = "Ej. Informe Final",
            isError = state.titleError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.titleError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        UniHubTextField(
            value = state.description,
            onValueChange = { viewModel.onEvent(TaskFormEvent.EnteredDescription(it)) },
            label = "Descripción (Opcional)",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
            UniHubTextField(
                value = state.dueDate,
                onValueChange = { },
                label = "Fecha de entrega",
                placeholder = "DD-MM-AAAA",
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = Icons.Default.CalendarToday
            )
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        
        UniHubButton(
            text = if (taskId == null) "Guardar Tarea" else "Guardar Cambios",
            onClick = { viewModel.onEvent(TaskFormEvent.SaveTask) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
