package com.unihub.app.features.subjects.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubProgressBar
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.subjects.presentation.viewmodel.SubjectDetailsViewModel
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskStatus
import java.util.Locale

@Composable
fun SubjectDetailsScreen(
    subjectId: String,
    onNavigateToEvent: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToCreateGrade: () -> Unit,
    onNavigateToEditGrade: (String) -> Unit,
    onNavigateToEditTask: (String) -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    viewModel: SubjectDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFabMenu by remember { mutableStateOf(false) }

    LaunchedEffect(subjectId) {
        viewModel.loadSubject(subjectId)
    }

    Scaffold(
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showFabMenu = true },
                    containerColor = UniHubTheme.colorScheme.primary,
                    contentColor = UniHubTheme.colorScheme.surface,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Acciones")
                }
                DropdownMenu(
                    expanded = showFabMenu,
                    onDismissRequest = { showFabMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nueva Tarea") },
                        onClick = { 
                            showFabMenu = false
                            onNavigateToCreateTask()
                        },
                        leadingIcon = { Icon(Icons.Default.Task, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Nueva Nota") },
                        onClick = { 
                            showFabMenu = false
                            onNavigateToCreateGrade()
                        },
                        leadingIcon = { Icon(Icons.Default.Grade, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Nuevo Evento") },
                        onClick = { 
                            showFabMenu = false
                            onNavigateToCreateEvent()
                        },
                        leadingIcon = { Icon(Icons.Default.Event, null) }
                    )
                }
            }
        },
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(UniHubTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            val subject = state.subject ?: return@Column

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)) {
                SubjectBadge(text = subject.code ?: "SIN CÓDIGO", color = UniHubTheme.colorScheme.primary)
                SubjectBadge(text = "Actual", color = UniHubTheme.colorScheme.success)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            
            Text(text = subject.name, style = UniHubTheme.typography.h1)
            Text(text = subject.professor ?: "Sin profesor", style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.textSecondary)
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            val totalWeight = state.grades.sumOf { it.weight }
            val weightedSum = state.grades.sumOf { it.value * it.weight }
            val avg = if (totalWeight > 0.0) weightedSum / totalWeight else 0.0

            // Average Card
            MetricCard(
                title = "Promedio Actual",
                subtitle = "Basado en ${state.grades.size} notas (${(totalWeight * 100).toInt()}% evaluado)",
                value = if (state.grades.isEmpty()) "---" else String.format(Locale.getDefault(), "%.1f / 5.0", avg),
                borderColor = UniHubTheme.colorScheme.accent
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            Button(
                onClick = onNavigateToSimulator,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UniHubTheme.colorScheme.accent.copy(alpha = 0.1f),
                    contentColor = UniHubTheme.colorScheme.accent
                ),
                shape = UniHubTheme.shape.md,
                contentPadding = PaddingValues(UniHubTheme.spacing.sm)
            ) {
                Icon(Icons.Default.Calculate, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
                Text("Simular nota necesaria", style = UniHubTheme.typography.label)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            MetricCard(
                title = "Progreso del curso",
                subtitle = "Peso total evaluado",
                value = "${(totalWeight * 100).toInt()}% completado",
                borderColor = UniHubTheme.colorScheme.secondary,
                showProgress = true,
                progress = totalWeight.toFloat().coerceIn(0f, 1f)
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            Text(text = "Siguiente clase", style = UniHubTheme.typography.h3)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            EventCard(
                name = "Clase Magistral",
                date = "Mañana, 24 Oct",
                time = "18:00 - 20:00",
                location = "Bloque 19 - 201",
                onClick = { onNavigateToEvent("1") }
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            if (state.tasks.isNotEmpty()) {
                Text(text = "Tareas", style = UniHubTheme.typography.h3)
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                state.tasks.forEach { task ->
                    TaskDetailItem(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onEdit = { onNavigateToEditTask(task.id) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            }
            
            if (state.grades.isNotEmpty()) {
                Text(text = "Calificaciones", style = UniHubTheme.typography.h3)
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                state.grades.forEach { grade ->
                    GradeDetailItem(
                        name = grade.name,
                        value = grade.value,
                        weight = grade.weight,
                        onEdit = { onNavigateToEditGrade(grade.id) },
                        onDelete = { viewModel.deleteGrade(grade.id) }
                    )
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                }
            }
        }
    }
}

@Composable
fun TaskDetailItem(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val isCompleted = task.status == TaskStatus.COMPLETED

    UniHubCard(padding = 0.dp) {
        Row(
            modifier = Modifier.padding(UniHubTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) UniHubTheme.colorScheme.success else UniHubTheme.colorScheme.textDisabled,
                modifier = Modifier.size(24.dp).clickable { onToggle() }
            )
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title, 
                    style = UniHubTheme.typography.h4,
                    color = if (isCompleted) UniHubTheme.colorScheme.textDisabled else UniHubTheme.colorScheme.textPrimary
                )
                Text(
                    text = "Vence: ${task.dueAt ?: "Sin fecha"}", 
                    style = UniHubTheme.typography.bodySmall, 
                    color = UniHubTheme.colorScheme.textSecondary
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, null, tint = UniHubTheme.colorScheme.textDisabled)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { showMenu = false; onEdit() })
                    DropdownMenuItem(text = { Text("Eliminar") }, onClick = { showMenu = false; onDelete() })
                }
            }
        }
    }
}

@Composable
fun GradeDetailItem(
    name: String,
    value: Double,
    weight: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    UniHubCard(padding = 0.dp) {
        Row(
            modifier = Modifier.padding(UniHubTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.AutoMirrored.Filled.Note, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                Column {
                    Text(text = name, style = UniHubTheme.typography.body)
                    Text(text = "Peso: ${(weight * 100).toInt()}%", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value.toString(), style = UniHubTheme.typography.h4, color = UniHubTheme.colorScheme.primary)
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = UniHubTheme.colorScheme.textDisabled)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Editar") }, onClick = { showMenu = false; onEdit() })
                        DropdownMenuItem(text = { Text("Eliminar") }, onClick = { showMenu = false; onDelete() })
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(UniHubTheme.shape.sm)
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = UniHubTheme.typography.label, color = color)
    }
}

@Composable
fun MetricCard(
    title: String,
    subtitle: String,
    value: String,
    borderColor: Color,
    showProgress: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit = {}
) {
    UniHubCard(padding = 0.dp, onClick = onClick) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(borderColor))
            Column(modifier = Modifier.padding(UniHubTheme.spacing.md).fillMaxWidth()) {
                Text(text = title, style = UniHubTheme.typography.h4)
                Text(text = subtitle, style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                
                if (showProgress) {
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                    UniHubProgressBar(progress = progress, color = borderColor)
                }
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
                Text(text = value, style = UniHubTheme.typography.h4, color = borderColor, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun EventCard(
    name: String,
    date: String,
    time: String,
    location: String,
    onClick: () -> Unit
) {
    UniHubCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarMonth, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
            Column {
                Text(text = name, style = UniHubTheme.typography.h4)
                Text(text = "$date | $time", style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                Text(text = location, style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
            }
        }
    }
}
