package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
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

enum class UniHubErrorType {
    NETWORK,
    SERVER,
    VALIDATION,
    AUTHENTICATION,
    UNKNOWN
}

@Composable
fun UniHubErrorState(
    type: UniHubErrorType = UniHubErrorType.UNKNOWN,
    title: String? = null,
    message: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (icon, defaultTitle, defaultMessage) = when (type) {
        UniHubErrorType.NETWORK -> Triple(
            Icons.Default.CloudOff,
            "Sin conexión",
            "No se pudo conectar al servidor. Por favor, verifica tu conexión a internet."
        )
        UniHubErrorType.SERVER -> Triple(
            Icons.Default.Error,
            "Error del servidor",
            "Ocurrió un error al procesar tu solicitud. Por favor, intenta de nuevo."
        )
        UniHubErrorType.VALIDATION -> Triple(
            Icons.Default.Warning,
            "Error de validación",
            "La información ingresada no es válida. Por favor, revisa los campos."
        )
        UniHubErrorType.AUTHENTICATION -> Triple(
            Icons.Default.Error,
            "Error de autenticación",
            "Tu sesión ha expirado. Por favor, inicia sesión nuevamente."
        )
        UniHubErrorType.UNKNOWN -> Triple(
            Icons.Default.Error,
            "Error",
            "Ocurrió un error inesperado. Por favor, intenta de nuevo."
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
                tint = UniHubTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            Text(
                text = title ?: defaultTitle,
                style = UniHubTheme.typography.h3,
                color = UniHubTheme.colorScheme.error,
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
