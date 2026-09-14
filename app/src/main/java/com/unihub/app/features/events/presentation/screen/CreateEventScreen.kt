package com.unihub.app.features.events.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.SelectOption
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.component.foundation.UniHubDialog
import com.unihub.app.core.designsystem.component.foundation.UniHubSelect
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.events.domain.model.EventReminder
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.model.ReminderType
import com.unihub.app.features.events.presentation.viewmodel.EventFormEvent
import com.unihub.app.features.events.presentation.viewmodel.EventFormViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEventScreen(
    eventId: String? = null,
    subjectId: String? = null,
    onEventCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: EventFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var showSubjectDropdown by remember { mutableStateOf(false) }
    
    var showMinutesPicker by remember { mutableStateOf(false) }
    var showHoursPicker by remember { mutableStateOf(false) }
    var showDaysPicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = subjectId) {
        if (subjectId != null && state.subjectId == null) {
            viewModel.onEvent(EventFormEvent.SubjectSelected(subjectId))
        }
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
                        onEventCreated()
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
                        viewModel.onEvent(EventFormEvent.EnteredDate(formatted))
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

    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        TimePickerDialogCustom(
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                val formatted = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                viewModel.onEvent(EventFormEvent.EnteredStartTime(formatted))
                showStartTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (showEndTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        TimePickerDialogCustom(
            onDismiss = { showEndTimePicker = false },
            onConfirm = {
                val formatted = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                viewModel.onEvent(EventFormEvent.EnteredEndTime(formatted))
                showEndTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (showSubjectDropdown) {
        UniHubDialog(
            onDismissRequest = { showSubjectDropdown = false }
        ) {
            Column {
                Text(
                    text = "Seleccionar Materia",
                    style = UniHubTheme.typography.h3
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                Text(
                    text = "Ninguna",
                    style = UniHubTheme.typography.body,
                    color = if (state.subjectId == null) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(EventFormEvent.SubjectSelected(null))
                            showSubjectDropdown = false
                        }
                        .padding(UniHubTheme.spacing.sm)
                )
                state.subjects.forEach { subject ->
                    Text(
                        text = subject.name,
                        style = UniHubTheme.typography.body,
                        color = if (state.subjectId == subject.id) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(EventFormEvent.SubjectSelected(subject.id))
                                showSubjectDropdown = false
                            }
                            .padding(UniHubTheme.spacing.sm)
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = when (data.visuals.message) {
                        is String -> UniHubTheme.colorScheme.surface
                        else -> UniHubTheme.colorScheme.surface
                    }
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
            text = if (eventId == null) "Crear Evento" else "Editar Evento",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

        UniHubTextField(
            value = state.title,
            onValueChange = { viewModel.onEvent(EventFormEvent.EnteredTitle(it)) },
            label = "Título del evento",
            isError = state.titleError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.titleError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        val subjectOptions = listOf(SelectOption<String?>(null, "Ninguna")) +
            state.subjects.map { SelectOption<String?>(it.id, it.name) }

        UniHubSelect(
            options = subjectOptions,
            selectedValue = state.subjectId,
            onOptionSelected = { subjectId ->
                viewModel.onEvent(EventFormEvent.SubjectSelected(subjectId))
            },
            label = "Materia (Opcional)",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text("Tipo de evento", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            EventType.entries.forEach { type ->
                val label = when (type) {
                    EventType.CLASS -> "Clase"
                    EventType.EXAM -> "Examen"
                    EventType.MEETING -> "Reunion"
                    EventType.PERSONAL -> "Personal"
                    EventType.OTHER -> "Otro"
                }
                UniHubChip(
                    label = label,
                    selected = state.eventType == type,
                    onClick = { viewModel.onEvent(EventFormEvent.EventTypeChanged(type)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
            UniHubTextField(
                value = state.date,
                onValueChange = {},
                label = "Fecha",
                placeholder = "DD-MM-AAAA",
                isError = state.dateError != null,
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = Icons.Default.CalendarToday
            )
        }
        state.dateError?.let {
            Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Row(horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.md)) {
            Box(modifier = Modifier.weight(1f).clickable { showStartTimePicker = true }) {
                UniHubTextField(
                    value = state.startTime,
                    onValueChange = {},
                    label = "Hora inicio",
                    placeholder = "Opcional",
                    isError = state.startTimeError != null,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    leadingIcon = Icons.Default.Schedule
                )
            }
            Box(modifier = Modifier.weight(1f).clickable { showEndTimePicker = true }) {
                UniHubTextField(
                    value = state.endTime,
                    onValueChange = {},
                    label = "Hora fin",
                    placeholder = "Opcional",
                    isError = state.endTimeError != null,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    leadingIcon = Icons.Default.Schedule
                )
            }
        }

        if (state.startTimeError != null || state.endTimeError != null) {
            Text(
                text = state.startTimeError ?: state.endTimeError ?: "",
                color = UniHubTheme.colorScheme.error,
                style = UniHubTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text("Tipo de ubicación", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            LocationType.entries.forEach { type ->
                val label = when (type) {
                    LocationType.PHYSICAL -> "Presencial"
                    LocationType.REMOTE -> "Remoto"
                    LocationType.NONE -> "Ninguna"
                }
                UniHubChip(
                    label = label,
                    selected = state.locationType == type,
                    onClick = { viewModel.onEvent(EventFormEvent.LocationTypeChanged(type)) }
                )
            }
        }

        if (state.locationType == LocationType.REMOTE) {
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            UniHubTextField(
                value = state.meetingUrl,
                onValueChange = { viewModel.onEvent(EventFormEvent.EnteredMeetingUrl(it)) },
                label = "Enlace de la reunión",
                placeholder = "https://...",
                isError = state.meetingUrlError != null,
                modifier = Modifier.fillMaxWidth()
            )
            state.meetingUrlError?.let {
                Text(text = it, color = UniHubTheme.colorScheme.error, style = UniHubTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        UniHubTextField(
            value = state.notes,
            onValueChange = { viewModel.onEvent(EventFormEvent.EnteredNotes(it)) },
            label = "Notas",
            placeholder = "Información adicional...",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        Text("Recordatorios", style = UniHubTheme.typography.h4, color = UniHubTheme.colorScheme.textPrimary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        Text(
            "Agrega recordatorios para este evento",
            style = UniHubTheme.typography.bodySmall,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

        state.reminders.forEach { reminder ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = reminder.getDisplayText(),
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    Switch(
                        checked = reminder.isEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onEvent(EventFormEvent.ReminderToggled(reminder.id, enabled))
                        }
                    )
                    IconButton(onClick = { viewModel.onEvent(EventFormEvent.ReminderRemoved(reminder.id)) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar recordatorio",
                            tint = UniHubTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            UniHubButton(
                text = "Minutos antes",
                onClick = { showMinutesPicker = true },
                modifier = Modifier.weight(1f)
            )
            UniHubButton(
                text = "Horas antes",
                onClick = { showHoursPicker = true },
                modifier = Modifier.weight(1f)
            )
            UniHubButton(
                text = "Días antes",
                onClick = { showDaysPicker = true },
                modifier = Modifier.weight(1f)
            )
        }

        if (showMinutesPicker) {
            NumberPickerDialog(
                title = "Minutos antes",
                maxValue = 59,
                minValue = 1,
                onDismiss = { showMinutesPicker = false },
                onConfirm = { value ->
                    val reminder = EventReminder(
                        id = UUID.randomUUID().toString(),
                        eventId = "",
                        reminderType = ReminderType.MINUTES_BEFORE,
                        value = value
                    )
                    viewModel.onEvent(EventFormEvent.ReminderAdded(reminder))
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
                    val reminder = EventReminder(
                        id = UUID.randomUUID().toString(),
                        eventId = "",
                        reminderType = ReminderType.HOURS_BEFORE,
                        value = value
                    )
                    viewModel.onEvent(EventFormEvent.ReminderAdded(reminder))
                    showHoursPicker = false
                }
            )
        }

        if (showDaysPicker) {
            NumberPickerDialog(
                title = "Días antes",
                maxValue = 30,
                minValue = 1,
                onDismiss = { showDaysPicker = false },
                onConfirm = { value ->
                    val reminder = EventReminder(
                        id = UUID.randomUUID().toString(),
                        eventId = "",
                        reminderType = ReminderType.DAYS_BEFORE,
                        value = value
                    )
                    viewModel.onEvent(EventFormEvent.ReminderAdded(reminder))
                    showDaysPicker = false
                }
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        UniHubButton(
            text = "Guardar Evento",
            onClick = { viewModel.onEvent(EventFormEvent.SaveEvent) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        )
    }
    }
}

@Composable
fun NumberPickerDialog(
    title: String,
    maxValue: Int,
    minValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedValue by remember { mutableIntStateOf(minValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    items((minValue..maxValue).toList()) { value ->
                        Text(
                            text = "$value",
                            style = UniHubTheme.typography.h3,
                            color = if (value == selectedValue) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.textPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedValue = value }
                                .padding(UniHubTheme.spacing.sm),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedValue) }) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun TimePickerDialogCustom(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Aceptar") }
        },
        text = { content() }
    )
}
