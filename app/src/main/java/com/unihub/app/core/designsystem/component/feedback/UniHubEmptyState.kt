package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

enum class UniHubEmptyStateType {
    NO_DATA,
    NO_RESULTS,
    NO_EVENTS,
    NO_TASKS,
    NO_SUBJECTS,
    NO_GRADES,
    CUSTOM
}

@Composable
fun UniHubEmptyState(
    type: UniHubEmptyStateType = UniHubEmptyStateType.NO_DATA,
    title: String? = null,
    message: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (icon, defaultTitle, defaultMessage) = when (type) {
        UniHubEmptyStateType.NO_DATA -> Triple(
            Icons.Default.Inbox,
            "Sin datos",
            "No hay información disponible en este momento."
        )
        UniHubEmptyStateType.NO_RESULTS -> Triple(
            Icons.Default.SearchOff,
            "Sin resultados",
            "No se encontraron resultados para tu búsqueda."
        )
        UniHubEmptyStateType.NO_EVENTS -> Triple(
            Icons.Default.EventBusy,
            "No hay eventos",
            "No tienes eventos programados. ¡Crea uno para comenzar!"
        )
        UniHubEmptyStateType.NO_TASKS -> Triple(
            Icons.Default.Inbox,
            "No hay tareas",
            "No tienes tareas pendientes. ¡Bien hecho!"
        )
        UniHubEmptyStateType.NO_SUBJECTS -> Triple(
            Icons.Default.HelpOutline,
            "No hay materias",
            "No has agregado materias todavía. ¡Comienza creando una!"
        )
        UniHubEmptyStateType.NO_GRADES -> Triple(
            Icons.Default.Inbox,
            "No hay calificaciones",
            "Aún no has registrado calificaciones para esta materia."
        )
        UniHubEmptyStateType.CUSTOM -> Triple(
            Icons.Default.Inbox,
            title ?: "Sin datos",
            message ?: "No hay información disponible."
        )
    }

    UniHubCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(UniHubTheme.spacing.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UniHubTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = UniHubTheme.colorScheme.textSecondary.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            Text(
                text = title ?: defaultTitle,
                style = UniHubTheme.typography.h3,
                color = UniHubTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            
            Text(
                text = message ?: defaultMessage,
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
            
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))
                
                UniHubButton(
                    text = actionLabel,
                    onClick = onAction,
                    variant = UniHubButtonVariant.Primary
                )
            }
        }
    }
}
