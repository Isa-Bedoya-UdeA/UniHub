package com.unihub.app.features.academic.presentation.screen

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun AcademicScreen(
    onNavigateToSubjectDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(UniHubTheme.spacing.md)
    ) {
        Text(
            text = "Mi Progreso",
            style = UniHubTheme.typography.h1,
            color = UniHubTheme.colorScheme.textPrimary
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        
        // Cumulative Card
        UniHubCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "GPA Acumulado",
                    style = UniHubTheme.typography.label,
                    color = UniHubTheme.colorScheme.textSecondary
                )
                Text(
                    text = "4.2",
                    style = UniHubTheme.typography.h1,
                    color = UniHubTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AcademicStat(label = "Créditos", value = "112 / 160")
                    AcademicStat(label = "Progreso", value = "70%")
                }
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                // Progress Bar Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(UniHubTheme.colorScheme.border)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(UniHubTheme.colorScheme.accent)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        Text(
            text = "Semestre Actual (2026-2)",
            style = UniHubTheme.typography.h3,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        // Subject Cards
        AcademicSubjectCard(
            code = "COM-101",
            name = "Computación Móvil",
            grade = "4.5",
            progress = 60,
            color = UniHubTheme.colorScheme.primary,
            onClick = { onNavigateToSubjectDetail("1") }
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        AcademicSubjectCard(
            code = "MAT-202",
            name = "Cálculo Integral",
            grade = "3.2",
            progress = 45,
            color = UniHubTheme.colorScheme.secondary,
            onClick = { onNavigateToSubjectDetail("2") }
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        AcademicSubjectCard(
            code = "BD-303",
            name = "Bases de Datos",
            grade = "4.0",
            progress = 30,
            color = UniHubTheme.colorScheme.accent,
            onClick = { onNavigateToSubjectDetail("3") }
        )
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
fun AcademicSubjectCard(
    code: String,
    name: String,
    grade: String,
    progress: Int,
    color: Color,
    onClick: () -> Unit
) {
    UniHubCard(padding = 0.dp, onClick = onClick) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(color))
            Row(
                modifier = Modifier.padding(UniHubTheme.spacing.md).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = code, style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
                    Text(text = name, style = UniHubTheme.typography.h4, color = UniHubTheme.colorScheme.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Evaluado: $progress%", style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                }
                
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grade,
                        style = UniHubTheme.typography.h4,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
