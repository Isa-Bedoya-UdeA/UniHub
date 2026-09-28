package com.unihub.app.features.tasks.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.common.state.UiEvent
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.domain.model.TaskReminderType
import com.unihub.app.features.tasks.presentation.viewmodel.TaskDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter

@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
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
                        onBack()
                    }
                }
                else -> {}
            }
        }
    }

    val task = state.task

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = UniHubTheme.colorScheme.background
    ) { paddingValues ->
        if (task == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = UniHubTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(UniHubTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = task.title,
                style = UniHubTheme.typography.h1,
                color = UniHubTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            Row(
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
            ) {
                val priorityLabel = when (task.priority) {
                    TaskPriority.LOW -> "Baja"
                    TaskPriority.MEDIUM -> "Media"
                    TaskPriority.HIGH -> "Alta"
                }
                val priorityColor = when (task.priority) {
                    TaskPriority.LOW -> UniHubTheme.colorScheme.success
                    TaskPriority.MEDIUM -> UniHubTheme.colorScheme.warning
                    TaskPriority.HIGH -> UniHubTheme.colorScheme.error
                }
                val statusLabel = when (task.status) {
                    TaskStatus.PENDING -> "Pendiente"
                    TaskStatus.IN_PROGRESS -> "En progreso"
                    TaskStatus.COMPLETED -> "Completada"
                }

                UniHubChip(
                    label = priorityLabel,
                    selected = true,
                    onClick = { }
                )
                UniHubChip(
                    label = statusLabel,
                    selected = task.status == TaskStatus.COMPLETED,
                    onClick = { viewModel.toggleStatus() }
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            if (task.dueAt != null) {
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Fecha de entrega",
                    value = task.dueAt
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            }

            if (task.reminderType != null && task.reminderValue != null) {
                val reminderText = when (task.reminderType) {
                    TaskReminderType.MINUTES_BEFORE -> "${task.reminderValue} min antes"
                    TaskReminderType.HOURS_BEFORE -> "${task.reminderValue} h antes"
                    TaskReminderType.DAYS_BEFORE -> "${task.reminderValue} dia${if (task.reminderValue != 1) "s" else ""} antes"
                }
                InfoRow(
                    icon = Icons.Default.Notifications,
                    label = "Recordatorio",
                    value = reminderText
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            }

            if (task.isDeadlineReminderEnabled) {
                InfoRow(
                    icon = Icons.Default.Alarm,
                    label = "Notificar vencimiento",
                    value = "Activado"
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            }

            if (task.description != null && task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                Text(
                    text = "Descripcion",
                    style = UniHubTheme.typography.h4,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
                UniHubCard(padding = UniHubTheme.spacing.md) {
                    Text(
                        text = task.description,
                        style = UniHubTheme.typography.body,
                        color = UniHubTheme.colorScheme.textPrimary
                    )
                }
            }

            if (state.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                Text(
                    text = "Etiquetas",
                    style = UniHubTheme.typography.h4,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
                ) {
                    state.tags.forEach { tag ->
                        UniHubChip(label = tag.name, selected = true, onClick = { })
                    }
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            UniHubButton(
                text = "Editar Tarea",
                onClick = { onEdit(task.id) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubButton(
                text = "Eliminar Tarea",
                variant = UniHubButtonVariant.Destructive,
                onClick = { viewModel.deleteTask() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = UniHubTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(text = label, style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
            Text(text = value, style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.textPrimary)
        }
    }
}
