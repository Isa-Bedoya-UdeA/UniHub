package com.unihub.app.features.subjects.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.academic.UniHubTaskItem
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubProgressBar
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun SubjectDetailsScreen(
    subjectId: String,
    onNavigateToEvent: (String) -> Unit,
    onBack: () -> Unit
) {
    var showFabMenu by remember { mutableStateOf(false) }

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
                        onClick = { showFabMenu = false },
                        leadingIcon = { Icon(Icons.Default.Task, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Nueva Nota") },
                        onClick = { showFabMenu = false },
                        leadingIcon = { Icon(Icons.Default.Grade, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Nuevo Evento") },
                        onClick = { showFabMenu = false },
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
            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)) {
                SubjectBadge(text = "COMP-204", color = UniHubTheme.colorScheme.primary)
                SubjectBadge(text = "Actual", color = UniHubTheme.colorScheme.success)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            
            Text(text = "Computación Móvil", style = UniHubTheme.typography.h1)
            Text(text = "Prof. Elena Rodriguez", style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.textSecondary)
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            // Average Card
            MetricCard(
                title = "Promedio Actual",
                subtitle = "Basado en 3 notas",
                value = "4.5 / 5.0",
                borderColor = UniHubTheme.colorScheme.accent
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Progress Card
            MetricCard(
                title = "Progreso del curso",
                subtitle = "Semana 10 de 16",
                value = "62% completado",
                borderColor = UniHubTheme.colorScheme.secondary,
                showProgress = true,
                progress = 0.62f
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            Text(text = "Siguiente clase", style = UniHubTheme.typography.h3)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Next Event Card
            EventCard(
                name = "Clase Magistral",
                date = "Mañana, 24 Oct",
                time = "18:00 - 20:00",
                location = "Bloque 19 - 201",
                onClick = { onNavigateToEvent("1") }
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            Text(text = "Tareas pendientes", style = UniHubTheme.typography.h3)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            UniHubTaskItem(
                title = "Informe de UI",
                dueDate = "Mañana",
                color = UniHubTheme.colorScheme.warning
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            Text(text = "Notas recientes", style = UniHubTheme.typography.h3)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            GradeItem(title = "Primer Parcial", grade = "4.8", icon = Icons.AutoMirrored.Filled.Note)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            GradeItem(title = "Laboratorio 1", grade = "4.2", icon = Icons.Default.Code)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            GradeItem(title = "Taller Final", grade = "?", icon = Icons.Default.Task)
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
    progress: Float = 0f
) {
    UniHubCard(padding = 0.dp) {
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

@Composable
fun GradeItem(title: String, grade: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    UniHubCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                Text(text = title, style = UniHubTheme.typography.body)
            }
            Text(
                text = grade,
                style = UniHubTheme.typography.h4,
                color = if (grade == "?") UniHubTheme.colorScheme.textDisabled else UniHubTheme.colorScheme.primary
            )
        }
    }
}
