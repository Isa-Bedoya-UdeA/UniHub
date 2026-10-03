package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubSnackbar(
    message: String,
    type: MessageType = MessageType.INFO,
    modifier: Modifier = Modifier
) {
    val (statusColor, icon) = when (type) {
        MessageType.SUCCESS -> UniHubTheme.colorScheme.success to Icons.Default.CheckCircle
        MessageType.ERROR -> UniHubTheme.colorScheme.error to Icons.Default.Error
        MessageType.WARNING -> UniHubTheme.colorScheme.warning to Icons.Default.Warning
        MessageType.INFO -> UniHubTheme.colorScheme.info to Icons.Default.Info
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = UniHubTheme.spacing.md, vertical = UniHubTheme.spacing.sm)
            .clip(RoundedCornerShape(12.dp))
            .background(statusColor)
            .padding(horizontal = UniHubTheme.spacing.md, vertical = UniHubTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
            Text(
                text = message,
                style = UniHubTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun UniHubSnackbarHost(
    hostState: SnackbarHostState,
    currentType: MessageType = MessageType.INFO,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        // Infer type from message content if error or success keywords present as fallback
        val effectiveType = when {
            data.visuals.message.startsWith("Error", ignoreCase = true) -> MessageType.ERROR
            data.visuals.message.contains("éxito", ignoreCase = true) || data.visuals.message.contains("exitosamente", ignoreCase = true) -> MessageType.SUCCESS
            else -> currentType
        }
        UniHubSnackbar(
            message = data.visuals.message,
            type = effectiveType
        )
    }
}
