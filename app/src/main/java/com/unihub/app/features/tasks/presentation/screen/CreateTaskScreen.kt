package com.unihub.app.features.tasks.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.events.presentation.screen.TimePickerDialogCustom
import com.unihub.app.features.tasks.presentation.viewmodel.TaskFormEvent
import com.unihub.app.features.tasks.presentation.viewmodel.TaskFormViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    subjectId: String? = null,
    taskId: String? = null,
    onTaskCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: TaskFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    
    var showReminderTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                    if (event.type == MessageType.SUCCESS) {
                        onTaskCreated()
                    }
                }
                else -> {}
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
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

    if (showReminderTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialogCustom(
            onDismiss = { showReminderTimePicker = false },
            onConfirm = {
                val today = java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                val formatted = String.format(Locale.getDefault(), "%sT%02d:%02d:00", today, timePickerState.hour, timePickerState.minute)
                viewModel.onEvent(TaskFormEvent.ReminderAtChanged(formatted))
                showReminderTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
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

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        // Deadline Reminder Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notificar vencimiento", style = UniHubTheme.typography.h4)
                Text(
                    "Avisar cuando expire el plazo",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.textSecondary
                )
            }
            Switch(
                checked = state.isDeadlineReminderEnabled,
                onCheckedChange = { viewModel.onEvent(TaskFormEvent.DeadlineReminderToggled(it)) }
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        // Custom Reminder
        Text("Recordatorio personalizado", style = UniHubTheme.typography.h4)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        Box(modifier = Modifier.fillMaxWidth().clickable { showReminderTimePicker = true }) {
            UniHubTextField(
                value = state.reminderAt?.split("T")?.lastOrNull()?.substring(0, 5) ?: "",
                onValueChange = { },
                label = "Hora del recordatorio",
                placeholder = "Opcional",
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = Icons.Default.Schedule
            )
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        
        UniHubButton(
            text = if (taskId == null) "Guardar Tarea" else "Guardar Cambios",
            onClick = { viewModel.onEvent(TaskFormEvent.SaveTask) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        )
    }
    }
}
