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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubChip
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun SubjectsScreen(
    onNavigateToDetails: (String) -> Unit
) {
    var selectedSemester by remember { mutableStateOf("2026-2") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Subject */ },
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

            // Subject Cards
            DetailedSubjectCard(
                code = "COMP-204",
                name = "Computación Móvil",
                professor = "Ing. Juan Perez",
                average = 4.5f,
                progressType = ProgressType.GOOD,
                onClick = { onNavigateToDetails("1") }
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            DetailedSubjectCard(
                code = "MAT-302",
                name = "Cálculo Integral",
                professor = "Dra. Maria Lopez",
                average = 3.2f,
                progressType = ProgressType.REGULAR,
                onClick = { onNavigateToDetails("2") }
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            DetailedSubjectCard(
                code = "BD-101",
                name = "Bases de Datos",
                professor = "Msc. Carlos Ruiz",
                average = 2.8f,
                progressType = ProgressType.BAD,
                onClick = { onNavigateToDetails("3") }
            )
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
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
                        .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = code,
                        style = UniHubTheme.typography.label,
                        color = UniHubTheme.colorScheme.primary
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
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = { showMenu = false }
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
                    text = "Promedio: $average / 5.0",
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textPrimary
                )

                val (icon, color) = when (progressType) {
                    ProgressType.GOOD -> Icons.AutoMirrored.Filled.ShowChart to UniHubTheme.colorScheme.success
                    ProgressType.REGULAR -> Icons.AutoMirrored.Filled.TrendingFlat to UniHubTheme.colorScheme.warning
                    ProgressType.BAD -> Icons.AutoMirrored.Filled.TrendingDown to UniHubTheme.colorScheme.error
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
