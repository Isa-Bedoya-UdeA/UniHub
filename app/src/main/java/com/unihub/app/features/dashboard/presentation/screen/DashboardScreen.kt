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
import com.unihub.app.features.events.domain.model.LocationType
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
    val state by viewModel.state.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Colombian Time
    val today = remember { LocalDate.now(ZoneId.systemDefault()) }
    val locale = Locale.forLanguageTag("es")
    val dateText = "${today.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }}, ${today.dayOfMonth} de ${today.month.getDisplayName(TextStyle.FULL, locale)}"
    
    val openMeetingUrl: (String) -> Unit = { url ->
        try {
            val uri = android.net.Uri.parse(url)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("DashboardScreen", "Error opening meeting URL", e)
        }
    }

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
                    Text(String.format(locale, "%.1f", state.summary.cumulativeGpa), style = UniHubTheme.typography.h1, color = UniHubTheme.colorScheme.primary)
                }
            }
            UniHubCard(
                modifier = Modifier.weight(1f),
                onClick = { /* Navigate to Academic */ }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Créditos", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
                    Text(state.summary.earnedCredits.toString(), style = UniHubTheme.typography.h1, color = UniHubTheme.colorScheme.secondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        // Upcoming Activities
        SectionHeader(title = "Próximas Actividades", onSeeAll = onNavigateToSubjects)
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        if (state.upcomingEvents.isEmpty()) {
            Text("No tienes actividades próximas.", style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.info)
        } else {
            val todayEvents = state.upcomingEvents.filter { event ->
                try {
                    val eventDateStr = event.startAt.split("T").firstOrNull() ?: ""
                    val eventDate = java.time.LocalDate.parse(eventDateStr)
                    eventDate.isEqual(today)
                } catch (e: Exception) {
                    false
                }
            }
            val otherEvents = state.upcomingEvents.filterNot { event ->
                try {
                    val eventDateStr = event.startAt.split("T").firstOrNull() ?: ""
                    val eventDate = java.time.LocalDate.parse(eventDateStr)
                    eventDate.isEqual(today)
                } catch (e: Exception) {
                    false
                }
            }
            
            if (todayEvents.isNotEmpty()) {
                Text(
                    text = "Hoy",
                    style = UniHubTheme.typography.label,
                    color = UniHubTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = UniHubTheme.spacing.xs)
                )
                todayEvents.take(3).forEach { event ->
                    ActivityCard(
                        title = event.title,
                        isClass = event.subjectId != null,
                        location = if (event.locationType == LocationType.PHYSICAL) event.notes else null,
                        meetingUrl = if (event.locationType == LocationType.REMOTE) event.meetingUrl else null,
                        time = if (event.startAt.contains("T")) "${event.startAt.split("T").last()} - ${event.endAt.split("T").last()}" else "Todo el día",
                        color = if (event.subjectId != null) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.accent,
                        onClick = { onNavigateToEvent(event.id) },
                        onMeetingUrlClick = openMeetingUrl
                    )
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                }
            }
            
            if (otherEvents.isNotEmpty() && todayEvents.size < 3) {
                if (todayEvents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                }
                Text(
                    text = "Próximos días",
                    style = UniHubTheme.typography.label,
                    color = UniHubTheme.colorScheme.textSecondary,
                    modifier = Modifier.padding(bottom = UniHubTheme.spacing.xs)
                )
                otherEvents.take(3 - todayEvents.size).forEach { event ->
                    ActivityCard(
                        title = event.title,
                        isClass = event.subjectId != null,
                        location = if (event.locationType == LocationType.PHYSICAL) event.notes else null,
                        meetingUrl = if (event.locationType == LocationType.REMOTE) event.meetingUrl else null,
                        time = if (event.startAt.contains("T")) "${event.startAt.split("T").last()} - ${event.endAt.split("T").last()}" else "Todo el día",
                        color = if (event.subjectId != null) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.accent,
                        onClick = { onNavigateToEvent(event.id) },
                        onMeetingUrlClick = openMeetingUrl
                    )
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                }
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Pending Tasks
        SectionHeader(title = "Tareas Pendientes", onSeeAll = onNavigateToTasks)
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        if (state.pendingTasks.isEmpty()) {
            Text(
                text = "No tienes tareas pendientes.",
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.success
            )
        } else {
            state.pendingTasks.take(3).forEach { task ->
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
    meetingUrl: String? = null,
    onMeetingUrlClick: ((String) -> Unit)? = null
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
                
                if (location != null || meetingUrl != null) {
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
                                .clickable { onMeetingUrlClick?.invoke(meetingUrl) }
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
