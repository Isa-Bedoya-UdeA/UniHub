package com.unihub.app.features.academic.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.academic.UniHubStudySelector
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.presentation.viewmodel.AcademicViewModel
import com.unihub.app.features.academic.presentation.viewmodel.SubjectWithGrade
import java.util.Locale

@Composable
fun AcademicScreen(
    onNavigateToSubjectDetail: (String) -> Unit,
    onNavigateToManagePeriods: () -> Unit,
    viewModel: AcademicViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val summary = state.summary
    val subjects = state.subjects

    Column(
        modifier = Modifier
            .fillMaxSize()
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
        
        // Cumulative Card
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
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AcademicStat(label = "Créditos", value = "${summary.earnedCredits} / ${summary.targetCredits}")
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

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        Text(
            text = "Semestre Actual",
            style = UniHubTheme.typography.h3,
            color = UniHubTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        if (subjects.isEmpty()) {
            Text(
                text = "No tienes materias registradas para el periodo seleccionado.",
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.info,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            subjects.forEach { item ->
                val subject = item.subject
                val color = try { Color(android.graphics.Color.parseColor(subject.color ?: "#4F46E5")) } catch (_: Exception) { UniHubTheme.colorScheme.primary }
                
                AcademicSubjectCard(
                    code = subject.code ?: "---",
                    name = subject.name,
                    grade = if (item.average > 0) String.format(Locale.getDefault(), "%.1f", item.average) else "---",
                    progress = item.progress,
                    color = color,
                    onClick = { onNavigateToSubjectDetail(subject.id) }
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
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
