package com.unihub.app.features.academic.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.academic.presentation.viewmodel.GradeSimulatorViewModel
import java.util.Locale

@Composable
fun GradeSimulatorScreen(
    subjectId: String,
    onBack: () -> Unit,
    viewModel: GradeSimulatorViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(subjectId) {
        viewModel.loadSubject(subjectId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.md)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Simulador de Notas",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.textPrimary
        )
        Text(
            text = state.subjectName,
            style = UniHubTheme.typography.body,
            color = UniHubTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        UniHubCard {
            Column(modifier = Modifier.padding(UniHubTheme.spacing.md)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SimulatorStat(label = "Promedio Actual", value = String.format(Locale.getDefault(), "%.2f", state.currentAverage))
                    SimulatorStat(label = "% Evaluado", value = "${(state.evaluatedWeight * 100).toInt()}%")
                }
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                LinearProgressIndicator(
                    progress = { state.evaluatedWeight.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = UniHubTheme.colorScheme.primary,
                    trackColor = UniHubTheme.colorScheme.border
                )
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))

        UniHubTextField(
            value = state.targetGrade.toString(),
            onValueChange = { viewModel.onTargetGradeChange(it) },
            label = "Nota Objetivo",
            placeholder = "3.0",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Results Card
        val resultColor = when {
            state.isAlreadyPassed -> UniHubTheme.colorScheme.success
            !state.isPossible -> UniHubTheme.colorScheme.error
            else -> UniHubTheme.colorScheme.primary
        }

        UniHubCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(UniHubTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        state.isAlreadyPassed -> "¡Materia Ganada!"
                        !state.isPossible -> "Objetivo Inalcanzable"
                        else -> "Necesitas"
                    },
                    style = UniHubTheme.typography.label,
                    color = resultColor
                )
                
                Text(
                    text = if (state.requiredGrade != null) String.format(Locale.getDefault(), "%.2f", state.requiredGrade.coerceAtLeast(0.0)) else "---",
                    style = UniHubTheme.typography.h1,
                    color = resultColor,
                    fontWeight = FontWeight.Black
                )
                
                Text(
                    text = "en el ${( (1.0 - state.evaluatedWeight) * 100).toInt()}% restante",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.textSecondary
                )
            }
        }

        if (!state.isPossible && !state.isAlreadyPassed) {
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(UniHubTheme.shape.md)
                    .background(UniHubTheme.colorScheme.error.copy(alpha = 0.1f))
                    .padding(UniHubTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, tint = UniHubTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
                Text(
                    "Incluso con un 5.0 en el resto de la materia, no alcanzarías la nota objetivo.",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SimulatorStat(label: String, value: String) {
    Column {
        Text(text = label, style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
        Text(text = value, style = UniHubTheme.typography.h3, color = UniHubTheme.colorScheme.textPrimary)
    }
}
