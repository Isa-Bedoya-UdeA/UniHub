package com.unihub.app.features.dashboard.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.unihub.app.features.tasks.domain.model.TaskStatus
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToSubjectDetail: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit
) {
    val summary by viewModel.academicSummary.collectAsState()
    val tasks by viewModel.pendingTasks.collectAsState()
    
    // Colombian Time
    val today = remember { LocalDate.now(ZoneId.of("America/Bogota")) }
    val locale = Locale.forLanguageTag("es")
    val dateText = "${today.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }}, ${today.dayOfMonth} de ${today.month.getDisplayName(TextStyle.FULL, locale)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(UniHubTheme.spacing.md)
    ) {
        // Date Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = UniHubTheme.spacing.xl)
        ) {
            Icon(
                Icons.Default.CalendarToday,
                null,
                tint = UniHubTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
            Text(
                text = dateText,
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.textSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Resumen Académico",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.md)
        ) {
            UniHubCard(
                modifier = Modifier.weight(1f),
                onClick = { /* Navigate to Academic */ }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Promedio", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
                    Text(String.format(locale, "%.1f", summary.cumulativeGpa), style = UniHubTheme.typography.h1, color = UniHubTheme.colorScheme.primary)
                }
            }
            UniHubCard(
                modifier = Modifier.weight(1f),
                onClick = { /* Navigate to Academic */ }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Créditos", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
                    Text(summary.totalCredits.toString(), style = UniHubTheme.typography.h1, color = UniHubTheme.colorScheme.secondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        // Upcoming Activities
        SectionHeader(title = "Próximas Actividades", onSeeAll = onNavigateToSubjects)
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        ActivityCard(
            title = "Computación Móvil",
            isClass = true,
            location = "Bloque 19 - 201",
            time = "08:00 - 10:00",
            color = UniHubTheme.colorScheme.primary,
            onClick = { onNavigateToSubjectDetail("1") }
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        ActivityCard(
            title = "Reunión Scrum",
            isClass = false,
            meetingUrl = "meet.google.com/abc-defg-hij",
            time = "14:00 - 15:00",
            color = UniHubTheme.colorScheme.accent,
            onClick = { onNavigateToEvent("2") }
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        // Pending Tasks
        SectionHeader(title = "Tareas Pendientes", onSeeAll = onNavigateToTasks)
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        if (tasks.isEmpty()) {
            Text(
                text = "No tienes tareas pendientes.",
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.textSecondary
            )
        } else {
            tasks.filter { it.status != TaskStatus.COMPLETED }.forEach { task ->
                TaskCard(
                    title = task.title, 
                    date = task.dueAt ?: "Sin fecha", 
                    color = UniHubTheme.colorScheme.warning,
                    isCompleted = false,
                    onToggle = { viewModel.toggleTaskCompletion(task) }
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = UniHubTheme.typography.h3, color = UniHubTheme.colorScheme.textPrimary)
        Text(
            text = "Ver todo",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.primary,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun ActivityCard(
    title: String,
    isClass: Boolean,
    time: String,
    color: Color,
    onClick: () -> Unit,
    location: String? = null,
    meetingUrl: String? = null
) {
    UniHubCard(padding = 0.dp, onClick = onClick) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(color))
            Column(modifier = Modifier.padding(UniHubTheme.spacing.md).weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = UniHubTheme.typography.h4)
                    if (isClass) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(UniHubTheme.colorScheme.secondary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clase", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.secondary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = time, style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (location != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(location, style = UniHubTheme.typography.bodySmall)
                    }
                } else if (meetingUrl != null) {
                    Row(
                        modifier = Modifier
                            .clip(UniHubTheme.shape.sm)
                            .background(UniHubTheme.colorScheme.primary.copy(0.1f))
                            .clickable { }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unirse a reunión", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    title: String, 
    date: String, 
    color: Color, 
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    UniHubCard(padding = 0.dp, onClick = onToggle) {
        Row(
            modifier = Modifier.padding(UniHubTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) UniHubTheme.colorScheme.success else UniHubTheme.colorScheme.textDisabled,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    style = UniHubTheme.typography.h4,
                    color = if (isCompleted) UniHubTheme.colorScheme.textDisabled else UniHubTheme.colorScheme.textPrimary
                )
                Text(
                    text = "Vence: $date",
                    style = UniHubTheme.typography.bodySmall, 
                    color = if (isCompleted) UniHubTheme.colorScheme.textDisabled else color
                )
            }
        }
    }
}
