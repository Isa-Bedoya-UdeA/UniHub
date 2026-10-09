package com.unihub.app.features.academic.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.academic.UniHubStudySelector
import com.unihub.app.core.designsystem.component.feedback.UniHubConfirmationDialog
import com.unihub.app.core.designsystem.component.feedback.UniHubEmptyState
import com.unihub.app.core.designsystem.component.feedback.UniHubEmptyStateType
import com.unihub.app.core.designsystem.component.feedback.UniHubLoadingState
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.presentation.viewmodel.AcademicViewModel
import com.unihub.app.features.academic.presentation.viewmodel.SubjectWithGrade
import java.util.Locale

@Composable
fun AcademicScreen(
    onNavigateToSubjectDetail: (String) -> Unit,
    onNavigateToCreateSubject: () -> Unit,
    onNavigateToEditSubject: (String) -> Unit,
    onNavigateToManagePeriods: () -> Unit,
    viewModel: AcademicViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val summary = state.summary
    val subjects = state.subjects
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    if (subjectToDelete != null) {
        UniHubConfirmationDialog(
            onDismissRequest = { subjectToDelete = null },
            onConfirm = {
                subjectToDelete?.let { viewModel.deleteSubject(it.id) }
                subjectToDelete = null
            },
            title = "Eliminar materia",
            message = "¿Estás seguro de que deseas eliminar la materia '${subjectToDelete?.name}'? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            isDestructive = true
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateSubject,
                containerColor = UniHubTheme.colorScheme.primary,
                contentColor = UniHubTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Materia")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi Progreso",
                    style = UniHubTheme.typography.h1,
                    color = UniHubTheme.colorScheme.textPrimary
                )

                Text(
                    text = "Gestionar Periodos",
                    style = UniHubTheme.typography.label,
                    color = UniHubTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToManagePeriods() }
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            UniHubStudySelector(
                studies = state.studies,
                selectedStudyId = state.selectedStudyId,
                onStudySelected = { viewModel.selectStudy(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            UniHubCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GPA Acumulado",
                        style = UniHubTheme.typography.label,
                        color = UniHubTheme.colorScheme.textSecondary
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f", summary.cumulativeGpa),
                        style = UniHubTheme.typography.h1,
                        color = UniHubTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )

                    if (summary.hasManualData) {
                        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
                        Text(
                            text = "Incluye datos de semestres anteriores",
                            style = UniHubTheme.typography.bodySmall,
                            color = UniHubTheme.colorScheme.info
                        )
                    }

                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AcademicStat(label = "Creditos", value = "${summary.earnedCredits} / ${summary.targetCredits}")
                        AcademicStat(label = "Progreso", value = "${summary.progressPercentage.toInt()}%")
                    }

                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(UniHubTheme.colorScheme.border)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(summary.progressPercentage.toFloat() / 100f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(UniHubTheme.colorScheme.accent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            if (state.periods.isEmpty()) {
                Text(
                    text = "No hay periodos registrados para este programa.",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.warning
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
                ) {
                    state.periods.forEach { period ->
                        UniHubChip(
                            label = period.name,
                            selected = state.selectedPeriodId == period.id,
                            onClick = { viewModel.selectPeriod(period.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            if (subjects.isEmpty()) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 4 }
                ) {
                    UniHubEmptyState(
                        type = UniHubEmptyStateType.NO_SUBJECTS,
                        actionLabel = "Crear Materia",
                        onAction = onNavigateToCreateSubject
                    )
                }
            } else {
                subjects.forEach { item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 4 }
                    ) {
                        val subject = item.subject
                        val colorHex = subject.color ?: "#4F46E5"

                        DetailedSubjectCard(
                            code = subject.code ?: "---",
                            name = subject.name,
                            professor = subject.professor ?: "Sin profesor",
                            average = item.average,
                            progress = item.progress,
                            colorHex = colorHex,
                            onClick = { onNavigateToSubjectDetail(subject.id) },
                            onEdit = { onNavigateToEditSubject(subject.id) },
                            onDelete = { subjectToDelete = subject }
                        )
                    }
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                }
            }
        }
    }
}

@Composable
fun AcademicStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Text(text = value, style = UniHubTheme.typography.h4, color = UniHubTheme.colorScheme.textPrimary)
    }
}

@Composable
fun DetailedSubjectCard(
    code: String,
    name: String,
    professor: String,
    average: Double,
    progress: Int,
    colorHex: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val color = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (_: Exception) { UniHubTheme.colorScheme.primary }

    UniHubCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        padding = 0.dp
    ) {
        Column(modifier = Modifier.padding(UniHubTheme.spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(UniHubTheme.shape.sm)
                        .background(color.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = code,
                        style = UniHubTheme.typography.label,
                        color = color
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = UniHubTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = UniHubTheme.colorScheme.error) }
                        )
                    }
                }
            }

            Text(text = name, style = UniHubTheme.typography.h3)
            Text(text = professor, style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            HorizontalDivider(color = UniHubTheme.colorScheme.border)
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (average > 0) String.format(Locale.getDefault(), "Promedio: %.1f / 5.0", average) else "Sin notas registradas",
                    style = UniHubTheme.typography.body,
                    color = if (average > 0) UniHubTheme.colorScheme.textPrimary else UniHubTheme.colorScheme.textDisabled
                )

                Text(
                    text = "Evaluado: $progress%",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.textSecondary
                )
            }
        }
    }
}
