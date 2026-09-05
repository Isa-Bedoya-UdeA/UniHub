package com.unihub.app.features.academic.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.presentation.viewmodel.AcademicViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicPeriodsScreen(
    onBack: () -> Unit,
    viewModel: AcademicViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val periods = state.periods
    
    var periodToDelete by remember { mutableStateOf<AcademicPeriod?>(null) }
    var periodToEdit by remember { mutableStateOf<AcademicPeriod?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (periodToDelete != null) {
        AlertDialog(
            onDismissRequest = { periodToDelete = null },
            title = { Text("Eliminar Periodo") },
            text = { Text("¿Estás seguro de que quieres eliminar '${periodToDelete?.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    periodToDelete?.let { viewModel.deletePeriod(it.id) }
                    periodToDelete = null
                }) { Text("Eliminar", color = UniHubTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { periodToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    if (showAddDialog || periodToEdit != null) {
        PeriodDialog(
            period = periodToEdit,
            onDismiss = { 
                showAddDialog = false
                periodToEdit = null
            },
            onSave = { name, start, end ->
                if (periodToEdit != null) {
                    viewModel.addPeriod(periodToEdit!!.copy(name = name, startDate = start, endDate = end))
                } else {
                    viewModel.addPeriod(AcademicPeriod(UUID.randomUUID().toString(), "user123", name, start, end, periods.isEmpty(), "", ""))
                }
                showAddDialog = false
                periodToEdit = null
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = UniHubTheme.colorScheme.primary,
                contentColor = UniHubTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Periodo")
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
                text = "Gestionar Periodos",
                style = UniHubTheme.typography.h2,
                color = UniHubTheme.colorScheme.textPrimary
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            if (periods.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay periodos configurados.", color = UniHubTheme.colorScheme.textSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm)
                ) {
                    items(periods) { period ->
                        UniHubCard {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(UniHubTheme.spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = period.name, style = UniHubTheme.typography.h4)
                                    Text(
                                        text = "${period.startDate} - ${period.endDate}",
                                        style = UniHubTheme.typography.bodySmall,
                                        color = UniHubTheme.colorScheme.textSecondary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.setCurrentPeriod(period.id) }) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Actual",
                                            tint = if (period.isCurrent) UniHubTheme.colorScheme.success else UniHubTheme.colorScheme.textDisabled
                                        )
                                    }
                                    IconButton(onClick = { periodToEdit = period }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = UniHubTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { periodToDelete = period }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = UniHubTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDialog(
    period: AcademicPeriod?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(period?.name ?: "") }
    var startDateText by remember { mutableStateOf(period?.startDate ?: "") }
    var endDateText by remember { mutableStateOf(period?.endDate ?: "") }
    
    val datePickerState = rememberDatePickerState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        startDateText = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    showStartDatePicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        endDateText = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    showEndDatePicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (period == null) "Nuevo Periodo" else "Editar Periodo") },
        text = {
            Column {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Nombre (Ej: 2024-2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = startDateText, 
                    onValueChange = {}, 
                    label = { Text("Inicio") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = endDateText, 
                    onValueChange = {}, 
                    label = { Text("Fin") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, null)
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onSave(name, startDateText, endDateText)
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
