package com.unihub.app.features.events.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.presentation.viewmodel.EventFormEvent
import com.unihub.app.features.events.presentation.viewmodel.EventFormViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    eventId: String? = null,
    onEventCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: EventFormViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = eventId) {
        viewModel.onEvent(EventFormEvent.Init(eventId))
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is EventFormViewModel.UiEvent.SaveSuccess -> {
                    onEventCreated()
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
        val timePickerState = rememberTimePickerState()
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
        val timePickerState = rememberTimePickerState()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
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
        
        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
            UniHubTextField(
                value = state.date,
                onValueChange = { },
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
                    onValueChange = { },
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
                    onValueChange = { },
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

        // Location Type Toggle
        Text("Tipo de ubicación", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm)
        ) {
            LocationType.values().forEach { type ->
                val label = when(type) {
                    LocationType.PHYSICAL -> "Presencial"
                    LocationType.REMOTE -> "Remoto"
                    LocationType.NONE -> "Ninguna"
                }
                FilterChip(
                    selected = state.locationType == type,
                    onClick = { viewModel.onEvent(EventFormEvent.LocationTypeChanged(type)) },
                    label = { Text(label) }
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
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        
        UniHubButton(
            text = "Guardar Evento",
            onClick = { viewModel.onEvent(EventFormEvent.SaveEvent) },
            modifier = Modifier.fillMaxWidth()
        )
    }
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
