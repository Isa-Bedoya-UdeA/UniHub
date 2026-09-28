package com.unihub.app.features.tasks.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.component.foundation.UniHubTextArea
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.events.presentation.screen.NumberPickerDialog
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskReminderType
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.presentation.viewmodel.TaskFormEvent
import com.unihub.app.features.tasks.presentation.viewmodel.TaskFormViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var showMinutesPicker by remember { mutableStateOf(false) }
    var showHoursPicker by remember { mutableStateOf(false) }
    var showDaysPicker by remember { mutableStateOf(false) }

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

    if (showMinutesPicker) {
        NumberPickerDialog(
            title = "Minutos antes",
            maxValue = 59,
            minValue = 1,
            onDismiss = { showMinutesPicker = false },
            onConfirm = { value ->
                viewModel.onEvent(TaskFormEvent.ReminderChanged(TaskReminderType.MINUTES_BEFORE, value))
                showMinutesPicker = false
            }
        )
    }

    if (showHoursPicker) {
        NumberPickerDialog(
            title = "Horas antes",
            maxValue = 23,
            minValue = 1,
            onDismiss = { showHoursPicker = false },
            onConfirm = { value ->
                viewModel.onEvent(TaskFormEvent.ReminderChanged(TaskReminderType.HOURS_BEFORE, value))
                showHoursPicker = false
            }
        )
    }

    if (showDaysPicker) {
        NumberPickerDialog(
            title = "Dias antes",
            maxValue = 30,
            minValue = 1,
            onDismiss = { showDaysPicker = false },
            onConfirm = { value ->
                viewModel.onEvent(TaskFormEvent.ReminderChanged(TaskReminderType.DAYS_BEFORE, value))
                showDaysPicker = false
            }
        )
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
            label = "Titulo de la tarea",
            placeholder = "Ej. Informe Final",
            isError = state.titleError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.titleError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        UniHubTextArea(
            value = state.description,
            onValueChange = { viewModel.onEvent(TaskFormEvent.EnteredDescription(it)) },
            label = "Descripcion (Opcional)",
            placeholder = "Detalles de la tarea...",
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

        Text("Prioridad", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            TaskPriority.entries.forEach { priority ->
                val label = when (priority) {
                    TaskPriority.LOW -> "Baja"
                    TaskPriority.MEDIUM -> "Media"
                    TaskPriority.HIGH -> "Alta"
                }
                UniHubChip(
                    label = label,
                    selected = state.priority == priority,
                    onClick = { viewModel.onEvent(TaskFormEvent.PriorityChanged(priority)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text("Estado", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            val statusLabels = mapOf(
                TaskStatus.PENDING to "Pendiente",
                TaskStatus.IN_PROGRESS to "En progreso",
                TaskStatus.COMPLETED to "Completada"
            )
            TaskStatus.entries.forEach { status ->
                UniHubChip(
                    label = statusLabels[status] ?: status.name,
                    selected = state.status == status,
                    onClick = { viewModel.onEvent(TaskFormEvent.StatusChanged(status)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text("Recordatorios", style = UniHubTheme.typography.h4, color = UniHubTheme.colorScheme.textPrimary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))

        state.reminderType?.let { type ->
            val displayText = when (type) {
                TaskReminderType.MINUTES_BEFORE -> "${state.reminderValue} min antes"
                TaskReminderType.HOURS_BEFORE -> "${state.reminderValue} h antes"
                TaskReminderType.DAYS_BEFORE -> "${state.reminderValue} dia${if ((state.reminderValue ?: 0) != 1) "s" else ""} antes"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = displayText, style = UniHubTheme.typography.body)
                IconButton(onClick = { viewModel.onEvent(TaskFormEvent.ClearReminder) }) {
                    Icon(Icons.Default.Close, "Eliminar recordatorio", tint = UniHubTheme.colorScheme.error)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            UniHubButton(
                text = "Min antes",
                onClick = { showMinutesPicker = true },
                modifier = Modifier.weight(1f)
            )
            UniHubButton(
                text = "H antes",
                onClick = { showHoursPicker = true },
                modifier = Modifier.weight(1f)
            )
            UniHubButton(
                text = "Dias antes",
                onClick = { showDaysPicker = true },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

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

        Text("Etiquetas", style = UniHubTheme.typography.h4, color = UniHubTheme.colorScheme.textPrimary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))

        if (state.selectedTags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xxs)
            ) {
                state.selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = { viewModel.onEvent(TaskFormEvent.RemoveTag(tag.id)) },
                        label = { Text(tag.name, style = UniHubTheme.typography.label) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, "Eliminar", modifier = Modifier.size(16.dp))
                        },
                        shape = UniHubTheme.shape.chip,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = UniHubTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = UniHubTheme.colorScheme.primary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        }

        UniHubTextField(
            value = state.tagInput,
            onValueChange = { viewModel.onEvent(TaskFormEvent.TagInputChanged(it)) },
            label = "Agregar etiqueta",
            placeholder = "Escribe y presiona Enter",
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Done,
            onImeAction = {
                if (state.tagInput.isNotBlank()) {
                    viewModel.onEvent(TaskFormEvent.AddTag(state.tagInput))
                }
            }
        )

        val filteredTags = viewModel.getFilteredTags(state.tagInput)
        if (filteredTags.isNotEmpty() && state.tagInput.isNotBlank()) {
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            Text(
                text = "Etiquetas existentes:",
                style = UniHubTheme.typography.label,
                color = UniHubTheme.colorScheme.textSecondary
            )
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xxs)
            ) {
                filteredTags.forEach { tag ->
                    AssistChip(
                        onClick = { viewModel.selectExistingTag(tag) },
                        label = { Text(tag.name, style = UniHubTheme.typography.label) },
                        shape = UniHubTheme.shape.chip,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = UniHubTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            labelColor = UniHubTheme.colorScheme.secondary
                        )
                    )
                }
            }
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
