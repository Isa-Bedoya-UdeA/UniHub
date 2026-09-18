package com.unihub.app.features.events.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.academic.UniHubLocationCard
import com.unihub.app.core.designsystem.component.academic.UniHubRemoteMeetingCard
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.presentation.viewmodel.EventDetailsViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EventDetailsScreen(
    eventId: String,
    onBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: EventDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val event = state.event
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { uiEvent ->
            if (uiEvent is UiEvent.ShowMessage) {
                snackbarHostState.showSnackbar(
                    message = uiEvent.message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    if (showDeleteDialog && event != null) {
        val isRecurring = event.recurrenceRuleId != null
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(if (isRecurring) "Eliminar Serie de Eventos" else "Eliminar Evento") },
            text = { 
                Text(
                    if (isRecurring) 
                        "Este es un evento recurrente. ¿Estás seguro de que deseas eliminar todas las ocurrencias de '${event.title}'? Esta acción no se puede deshacer."
                    else 
                        "¿Estás seguro de que deseas eliminar el evento '${event.title}'? Esta acción no se puede deshacer."
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEvent(onDeleted = onBack)
                    }
                ) {
                    Text("Eliminar", color = UniHubTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(UniHubTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            if (event == null) {
                Text("Evento no encontrado", style = UniHubTheme.typography.h2)
                return@Scaffold
            }

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)) {
                TypeBadge(text = if (event.subjectId != null) "Materia" else "Personal", color = UniHubTheme.colorScheme.primary)
                TypeBadge(
                    text = when(event.locationType) {
                        LocationType.PHYSICAL -> "Presencial"
                        LocationType.REMOTE -> "Remoto"
                        LocationType.NONE -> "General"
                    },
                    color = UniHubTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            
            Text(text = event.title, style = UniHubTheme.typography.h1)
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            // DateTime Card
            UniHubCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccessTime, null, tint = UniHubTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                    Column {
                        Text(text = event.startAt.split("T").first(), style = UniHubTheme.typography.h4)
                        val timeRange = if (event.startAt.contains("T")) {
                            "${event.startAt.split("T").last()} - ${event.endAt.split("T").last()}"
                        } else {
                            "Todo el día"
                        }
                        Text(text = timeRange, style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.textSecondary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Location Logic
            if (event.locationType == LocationType.PHYSICAL) {
                state.location?.let { location ->
                    UniHubLocationCard(
                        place = location.name ?: "Ubicación",
                        room = "",
                        building = location.address ?: "",
                        onOpenInMaps = { viewModel.openInMaps(context) }
                    )
                } ?: run {
                    UniHubCard {
                        Text(
                            text = "Sin detalles de ubicación",
                            style = UniHubTheme.typography.bodySmall,
                            color = UniHubTheme.colorScheme.textSecondary,
                            modifier = Modifier.padding(UniHubTheme.spacing.md)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            }
            
            if (event.locationType == LocationType.REMOTE) {
                UniHubRemoteMeetingCard(onJoinMeeting = { viewModel.openMeetingUrl(context) })
                event.meetingUrl?.let {
                    Text(text = it, style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                }
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            }
            
            if (event.notes != null && event.locationType != LocationType.PHYSICAL) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                UniHubCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.StickyNote2, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                            Text(text = "Notas", style = UniHubTheme.typography.label)
                        }
                        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                        Text(text = event.notes, style = UniHubTheme.typography.bodySmall)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Reminders Section
            if (event.reminders.isNotEmpty()) {
                UniHubCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, tint = UniHubTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                            Text(text = "Recordatorios", style = UniHubTheme.typography.h4)
                        }
                        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                        event.reminders.forEach { reminder ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = reminder.getDisplayText(),
                                    style = UniHubTheme.typography.body,
                                    color = if (reminder.isEnabled) UniHubTheme.colorScheme.textPrimary 
                                    else UniHubTheme.colorScheme.textDisabled
                                )
                                Text(
                                    text = if (reminder.isEnabled) "Activo" else "Inactivo",
                                    style = UniHubTheme.typography.bodySmall,
                                    color = if (reminder.isEnabled) UniHubTheme.colorScheme.success 
                                    else UniHubTheme.colorScheme.textDisabled
                                )
                            }
                            if (reminder != event.reminders.last()) {
                                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
                            }
                        }
                    }
                }
            } else {
                UniHubCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = UniHubTheme.colorScheme.textDisabled)
                        Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                        Text(text = "Sin recordatorios", style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.textDisabled)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            UniHubButton(
                text = "Editar Evento",
                variant = UniHubButtonVariant.Outlined,
                onClick = { onNavigateToEdit(event.id) },
                leadingIcon = Icons.Default.Edit,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubButton(
                text = "Eliminar Evento",
                variant = UniHubButtonVariant.Destructive,
                onClick = { showDeleteDialog = true },
                leadingIcon = Icons.Default.Delete,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TypeBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(UniHubTheme.shape.sm)
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = UniHubTheme.typography.label, color = color)
    }
}
