package com.unihub.app.features.calendar.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.calendar.presentation.viewmodel.CalendarViewModel
import com.unihub.app.features.dashboard.presentation.screen.ActivityCard
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.model.RecurrenceRule
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToEvent: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    // Correct Timezone for Colombia
    val today = remember { LocalDate.now(ZoneId.systemDefault()) }
    var selectedView by remember { mutableStateOf(CalendarView.Month) }
    var selectedDate by remember { mutableStateOf(today) }
    
    val state by viewModel.state.collectAsState()
    val events = state.events

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = UniHubTheme.colorScheme.primary,
                contentColor = UniHubTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Evento")
            }
        },
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(UniHubTheme.spacing.md)
        ) {
            Text(
                text = "Mi Agenda",
                style = UniHubTheme.typography.h1,
                color = UniHubTheme.colorScheme.textPrimary
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
                ) {
                    UniHubChip(
                        label = "Mes",
                        selected = selectedView == CalendarView.Month,
                        onClick = { selectedView = CalendarView.Month }
                    )
                    UniHubChip(
                        label = "Semana",
                        selected = selectedView == CalendarView.Week,
                        onClick = { selectedView = CalendarView.Week }
                    )
                    UniHubChip(
                        label = "Día",
                        selected = selectedView == CalendarView.Day,
                        onClick = { selectedView = CalendarView.Day }
                    )
                }

                Row {
                    IconButton(onClick = {
                        selectedDate = when (selectedView) {
                            CalendarView.Month -> selectedDate.minusMonths(1)
                            CalendarView.Week -> selectedDate.minusWeeks(1)
                            CalendarView.Day -> selectedDate.minusDays(1)
                        }
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = UniHubTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        selectedDate = when (selectedView) {
                            CalendarView.Month -> selectedDate.plusMonths(1)
                            CalendarView.Week -> selectedDate.plusWeeks(1)
                            CalendarView.Day -> selectedDate.plusDays(1)
                        }
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = UniHubTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            when (selectedView) {
                CalendarView.Month -> MonthlyView(selectedDate, events, onNavigateToEvent) { selectedDate = it }
                CalendarView.Week -> WeeklyView(selectedDate, events, onNavigateToEvent) { selectedDate = it }
                CalendarView.Day -> DailyView(selectedDate, events, onNavigateToEvent)
            }
        }
    }
}

@Composable
fun MonthlyView(currentDate: LocalDate, events: List<Event>, onEventClick: (String) -> Unit, onDateSelected: (LocalDate) -> Unit) {
    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
    val daysInMonth = (1..lastDayOfMonth.dayOfMonth).toList()
    val locale = Locale.forLanguageTag("es")
    val today = LocalDate.now(ZoneId.systemDefault())
    
    Column {
        Text(
            text = "${currentDate.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }} ${currentDate.year}",
            style = UniHubTheme.typography.h3,
            color = UniHubTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = UniHubTheme.spacing.md)
        )
        
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("L", "M", "M", "J", "V", "S", "D")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = UniHubTheme.typography.label,
                    color = UniHubTheme.colorScheme.textSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))

        val firstDayOffset = firstDayOfMonth.dayOfWeek.value - 1
        val totalCells = firstDayOffset + daysInMonth.size
        val rows = (totalCells + 6) / 7
        val cellSize = 36.dp
        val gridHeight = (rows * 36).dp

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(gridHeight),
            userScrollEnabled = false
        ) {
            items(firstDayOffset) {
                Box(modifier = Modifier.size(cellSize))
            }
            
            items(daysInMonth) { day ->
                val date = firstDayOfMonth.withDayOfMonth(day)
                val isToday = date == today
                val isSelected = date == currentDate
                val hasEvent = events.any { isEventOnDate(it, date) }

                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> UniHubTheme.colorScheme.primary
                                isToday -> UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day.toString(),
                            style = UniHubTheme.typography.bodySmall,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) UniHubTheme.colorScheme.surface else UniHubTheme.colorScheme.textPrimary
                        )
                        
                        if (hasEvent) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 1.dp)
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) UniHubTheme.colorScheme.surface else UniHubTheme.colorScheme.accent)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))
        Text(text = "Eventos del día", style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
        DailyEvents(currentDate, events, onEventClick)
    }
}

@Composable
fun WeeklyView(currentDate: LocalDate, events: List<Event>, onEventClick: (String) -> Unit, onDateSelected: (LocalDate) -> Unit) {
    val startOfWeek = currentDate.minusDays((currentDate.dayOfWeek.value - 1).toLong())
    val locale = Locale.forLanguageTag("es")
    val today = LocalDate.now(ZoneId.systemDefault())
    
    Column {
        Text(
            text = "Semana del ${startOfWeek.dayOfMonth} de ${startOfWeek.month.getDisplayName(TextStyle.SHORT, locale)}",
            style = UniHubTheme.typography.h3,
            color = UniHubTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = UniHubTheme.spacing.md)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..6).forEach { i ->
                val date = startOfWeek.plusDays(i.toLong())
                val isSelected = date == currentDate
                val isToday = date == today
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(UniHubTheme.shape.md)
                        .background(if (isSelected) UniHubTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onDateSelected(date) }
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).first().uppercase(),
                        style = UniHubTheme.typography.label,
                        color = if (isSelected) UniHubTheme.colorScheme.surface else UniHubTheme.colorScheme.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = UniHubTheme.typography.body,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) UniHubTheme.colorScheme.surface else if (isToday) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.textPrimary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        DailyEvents(currentDate, events, onEventClick)
    }
}

@Composable
fun DailyView(date: LocalDate, events: List<Event>, onEventClick: (String) -> Unit) {
    val locale = Locale.forLanguageTag("es")
    Column {
        Text(
            text = "${date.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }}, ${date.dayOfMonth} de ${date.month.getDisplayName(TextStyle.FULL, locale)}",
            style = UniHubTheme.typography.h3,
            color = UniHubTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = UniHubTheme.spacing.md)
        )
        DailyEvents(date, events, onEventClick)
    }
}

@Composable
fun DailyEvents(date: LocalDate, events: List<Event>, onEventClick: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dayEvents = events.filter { isEventOnDate(it, date) }
    
    val openMeetingUrl: (String) -> Unit = { url ->
        try {
            val uri = android.net.Uri.parse(url)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("CalendarScreen", "Error opening meeting URL", e)
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.md)
    ) {
        if (dayEvents.isEmpty()) {
            Text(
                text = "No tienes eventos programados para este día.",
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.info,
                modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            dayEvents.forEach { event ->
                ActivityCard(
                    title = event.title,
                    isClass = event.subjectId != null,
                    time = if (event.startAt.contains("T")) "${event.startAt.split("T").last()} - ${event.endAt.split("T").last()}" else "Todo el día",
                    location = if (event.locationType == LocationType.PHYSICAL) event.notes else null,
                    meetingUrl = if (event.locationType == LocationType.REMOTE) event.meetingUrl else null,
                    color = if (event.subjectId != null) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.accent,
                    onClick = { onEventClick(event.id) },
                    onMeetingUrlClick = openMeetingUrl
                )
            }
        }
    }
}

fun isEventOnDate(event: Event, date: LocalDate): Boolean {
    // Check direct date (supports yyyy-MM-dd format)
    if (event.startAt.startsWith(date.toString())) return true

    // Check recurrence
    val rule = event.recurrenceRule ?: return false
    val days = event.recurrenceDays
    
    try {
        val startDate = if (rule.startDate.contains("-")) {
            val parts = rule.startDate.split("-")
            if (parts[0].length == 4) LocalDate.parse(rule.startDate) // yyyy-MM-dd
            else LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt()) // dd-MM-yyyy
        } else return false
        
        val endDate = if (rule.endDate.contains("-")) {
            val parts = rule.endDate.split("-")
            if (parts[0].length == 4) LocalDate.parse(rule.endDate) // yyyy-MM-dd
            else LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt()) // dd-MM-yyyy
        } else return false

        if (date.isBefore(startDate) || date.isAfter(endDate)) return false

        // Currently we only support WEEKLY frequency with days
        if (rule.frequency == "WEEKLY") {
            val dayOfWeek = date.dayOfWeek.value // 1 (Mon) to 7 (Sun)
            return dayOfWeek in days
        }
    } catch (e: Exception) {
        android.util.Log.e("CalendarScreen", "Error checking event date", e)
        return false
    }

    return false
}

enum class CalendarView {
    Month, Week, Day
}
