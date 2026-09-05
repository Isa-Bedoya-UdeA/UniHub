package com.unihub.app.features.subjects.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.subjects.presentation.viewmodel.SubjectsViewModel

@Composable
fun SubjectsScreen(
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    var selectedSemester by remember { mutableStateOf("2026-2") }
    val subjects by viewModel.subjects.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
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
                .padding(UniHubTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Materias",
                style = UniHubTheme.typography.h1,
                color = UniHubTheme.colorScheme.textPrimary
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
            ) {
                val semesters = listOf("2026-2", "2026-1", "2025-2")
                semesters.forEach { semester ->
                    UniHubChip(
                        label = semester,
                        selected = selectedSemester == semester,
                        onClick = { selectedSemester = semester }
                    )
                }
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

            if (subjects.isEmpty()) {
                Text(
                    text = "No tienes materias registradas para este semestre.",
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textSecondary,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                subjects.forEach { subject ->
                    DetailedSubjectCard(
                        code = subject.code ?: "---",
                        name = subject.name,
                        professor = subject.professor ?: "Sin profesor",
                        average = 0.0f, // Logic for average calculation could be added
                        progressType = ProgressType.REGULAR,
                        colorHex = subject.color ?: "#4F46E5",
                        onClick = { onNavigateToDetails(subject.id) },
                        onEdit = { onNavigateToEdit(subject.id) },
                        onDelete = { viewModel.deleteSubject(subject.id) }
                    )
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                }
            }
        }
    }
}

enum class ProgressType { GOOD, REGULAR, BAD }

@Composable
fun DetailedSubjectCard(
    code: String,
    name: String,
    professor: String,
    average: Float,
    progressType: ProgressType,
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
                // Code Badge
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
                            leadingIcon = { Icon(Icons.Default.MoreVert, null) } // Replace with Edit icon if available
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = { 
                                showMenu = false 
                                onDelete()
                            }
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
                    text = if (average > 0) "Promedio: $average / 5.0" else "Sin notas registradas",
                    style = UniHubTheme.typography.body,
                    color = if (average > 0) UniHubTheme.colorScheme.textPrimary else UniHubTheme.colorScheme.textDisabled
                )

                val (icon, trendColor) = when (progressType) {
                    ProgressType.GOOD -> Icons.AutoMirrored.Filled.ShowChart to UniHubTheme.colorScheme.success
                    ProgressType.REGULAR -> Icons.AutoMirrored.Filled.TrendingFlat to UniHubTheme.colorScheme.warning
                    ProgressType.BAD -> Icons.AutoMirrored.Filled.TrendingDown to UniHubTheme.colorScheme.error
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
