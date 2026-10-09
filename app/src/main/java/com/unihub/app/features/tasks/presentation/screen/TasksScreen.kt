package com.unihub.app.features.tasks.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.feedback.UniHubEmptyState
import com.unihub.app.core.designsystem.component.feedback.UniHubEmptyStateType
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.presentation.viewmodel.TasksViewModel

@Composable
fun TasksScreen(
    onNavigateToCreate: () -> Unit = {},
    onNavigateToTaskDetail: (String) -> Unit = {},
    viewModel: TasksViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todas") }

    val filteredTasks = when (selectedFilter) {
        "Pendientes" -> tasks.filter { it.status == TaskStatus.PENDING }
        "En progreso" -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
        "Completadas" -> tasks.filter { it.status == TaskStatus.COMPLETED }
        else -> tasks
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = UniHubTheme.colorScheme.primary,
                contentColor = UniHubTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
            }
        },
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(UniHubTheme.spacing.md)
        ) {
            Text(
                text = "Mis Tareas",
                style = UniHubTheme.typography.h1,
                color = UniHubTheme.colorScheme.textPrimary
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
            ) {
                val filters = listOf("Todas", "Pendientes", "En progreso", "Completadas")
                filters.forEach { filter ->
                    UniHubChip(
                        label = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.md)
            ) {
                if (filteredTasks.isEmpty()) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 4 }
                    ) {
                        val emptyType = when (selectedFilter) {
                            "Todas" -> UniHubEmptyStateType.NO_TASKS
                            "Pendientes" -> UniHubEmptyStateType.NO_TASKS
                            "En progreso" -> UniHubEmptyStateType.NO_TASKS
                            "Completadas" -> UniHubEmptyStateType.NO_TASKS
                            else -> UniHubEmptyStateType.NO_DATA
                        }
                        UniHubEmptyState(
                            type = emptyType,
                            actionLabel = if (selectedFilter == "Todas") "Crear Tarea" else null,
                            onAction = if (selectedFilter == "Todas") onNavigateToCreate else null
                        )
                    }
                } else {
                    filteredTasks.forEachIndexed { index, task ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { it / 4 }
                        ) {
                            val priorityColor = when (task.priority) {
                                TaskPriority.HIGH -> UniHubTheme.colorScheme.error
                                TaskPriority.MEDIUM -> UniHubTheme.colorScheme.warning
                                TaskPriority.LOW -> UniHubTheme.colorScheme.success
                            }
                            DetailedTaskCard(
                                title = task.title,
                                subject = viewModel.getSubjectName(task.subjectId),
                                date = task.dueAt ?: "Sin fecha",
                                priorityColor = priorityColor,
                                isCompleted = task.status == TaskStatus.COMPLETED,
                                onToggle = { viewModel.toggleTaskCompletion(task) },
                                onClick = { onNavigateToTaskDetail(task.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedTaskCard(
    title: String,
    subject: String,
    date: String,
    priorityColor: Color,
    isCompleted: Boolean,
    onToggle: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    UniHubCard(
        padding = 0.dp,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Priority Indicator
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(if (isCompleted) UniHubTheme.colorScheme.textDisabled else priorityColor)
            )
            
            Row(
                modifier = Modifier
                    .padding(UniHubTheme.spacing.md)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio Button - clickable separately to toggle status
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isCompleted) UniHubTheme.colorScheme.success else UniHubTheme.colorScheme.textDisabled,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onToggle() }
                )
                
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = UniHubTheme.typography.h4,
                        color = if (isCompleted) UniHubTheme.colorScheme.textDisabled else UniHubTheme.colorScheme.textPrimary
                    )
                    Text(
                        text = subject,
                        style = UniHubTheme.typography.label,
                        color = if (isCompleted) UniHubTheme.colorScheme.textDisabled else UniHubTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = UniHubTheme.typography.bodySmall,
                        color = if (isCompleted) UniHubTheme.colorScheme.textDisabled else priorityColor
                    )
                }
            }
        }
    }
}
