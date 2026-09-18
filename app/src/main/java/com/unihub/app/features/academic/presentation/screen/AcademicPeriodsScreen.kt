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
import com.unihub.app.core.designsystem.component.foundation.UniHubConfirmationDialog
import com.unihub.app.core.designsystem.component.foundation.UniHubAlert
import com.unihub.app.core.designsystem.component.foundation.UniHubAlertVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubDialog
import com.unihub.app.core.designsystem.component.foundation.UniHubDropdown
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.presentation.viewmodel.AcademicViewModel
import java.time.Instant
import java.time.LocalDate
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
    val selectedStudyId = state.selectedStudyId
    val hasStudies = state.studies.isNotEmpty()

    var periodToDelete by remember { mutableStateOf<AcademicPeriod?>(null) }
    var periodToEdit by remember { mutableStateOf<AcademicPeriod?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (periodToDelete != null) {
        val subjectCount = state.periodSubjectCounts[periodToDelete?.id] ?: 0
        val extraMessage = if (subjectCount > 0) {
            "\n\nEste periodo contiene $subjectCount materia(s) que también serán eliminadas."
        } else ""
        
        UniHubConfirmationDialog(
            onDismissRequest = { periodToDelete = null },
            onConfirm = {
                periodToDelete?.let { viewModel.deletePeriod(it.id) }
                periodToDelete = null
            },
            title = "Eliminar Periodo",
            message = "¿Estás seguro de que quieres eliminar '${periodToDelete?.name}'? Esta acción no se puede deshacer.$extraMessage",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            isDestructive = true
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
                } else if (selectedStudyId != null) {
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    val isCurrentCandidate = try {
                        val startDate = LocalDate.parse(start, formatter)
                        val endDate = LocalDate.parse(end, formatter)
                        val today = LocalDate.now()
                        !today.isBefore(startDate) && !today.isAfter(endDate)
                    } catch (e: Exception) {
                        periods.isEmpty()
                    }

                    viewModel.addPeriod(
                        AcademicPeriod(
                            id = UUID.randomUUID().toString(),
                            userId = "current_user",
                            studyId = selectedStudyId,
                            name = name,
                            startDate = start,
                            endDate = end,
                            isCurrent = isCurrentCandidate,
                            createdAt = Instant.now().toString(),
                            updatedAt = Instant.now().toString()
                        )
                    )
                }
                showAddDialog = false
                periodToEdit = null
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (hasStudies) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = UniHubTheme.colorScheme.primary,
                    contentColor = UniHubTheme.colorScheme.surface,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Periodo")
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
        ) {
            Text(
                text = "Gestionar Periodos",
                style = UniHubTheme.typography.h2,
                color = UniHubTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            if (hasStudies) {
                val selectedStudy = state.studies.find { it.id == selectedStudyId }
                UniHubDropdown(
                    options = state.studies,
                    selectedOption = selectedStudy,
                    onOptionSelected = { viewModel.selectStudy(it.id) },
                    label = "Seleccionar Programa Académico",
                    optionToString = { "${it.name} (${it.institution})" }
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))
            }

            if (!hasStudies) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    UniHubAlert(
                        variant = UniHubAlertVariant.Warning,
                        title = "No hay programas académicos",
                        message = "Debes crear al menos un programa académico en Ajustes antes de poder añadir periodos.",
                        icon = Icons.Default.Info
                    )
                }
            } else if (periods.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    UniHubAlert(
                        variant = UniHubAlertVariant.Info,
                        title = "Sin periodos",
                        message = "No hay periodos configurados para este programa. Usa el botón + para añadir uno.",
                        icon = Icons.Default.Info
                    )
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
    var nameError by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
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
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        endDateText = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    showEndDatePicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    UniHubDialog(onDismissRequest = onDismiss) {
        Column {
            Text(
                text = if (period == null) "Nuevo Periodo" else "Editar Periodo",
                style = UniHubTheme.typography.h3
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

            UniHubTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = "Nombre",
                placeholder = "Ej: 2024-2",
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = nameError!!,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubTextField(
                value = startDateText,
                onValueChange = {},
                label = "Fecha de inicio",
                placeholder = "Seleccionar fecha",
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha inicio")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubTextField(
                value = endDateText,
                onValueChange = {},
                label = "Fecha de fin",
                placeholder = "Seleccionar fecha",
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha fin")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                UniHubButton(
                    text = "Cancelar",
                    variant = UniHubButtonVariant.Text,
                    onClick = onDismiss
                )
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                UniHubButton(
                    text = "Guardar",
                    variant = UniHubButtonVariant.Primary,
                    onClick = {
                        if (name.isBlank()) {
                            nameError = "El nombre es obligatorio"
                        } else {
                            onSave(name, startDateText, endDateText)
                        }
                    }
                )
            }
        }
    }
}
