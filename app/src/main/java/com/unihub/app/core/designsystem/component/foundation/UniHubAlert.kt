package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.unihub.app.ui.theme.UniHubTheme

enum class UniHubAlertVariant {
    Info,
    Success,
    Warning,
    Error
}

@Composable
fun UniHubAlert(
    variant: UniHubAlertVariant,
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    val backgroundColor = when (variant) {
        UniHubAlertVariant.Info -> UniHubTheme.colorScheme.info.copy(alpha = 0.1f)
        UniHubAlertVariant.Success -> UniHubTheme.colorScheme.success.copy(alpha = 0.1f)
        UniHubAlertVariant.Warning -> UniHubTheme.colorScheme.warning.copy(alpha = 0.1f)
        UniHubAlertVariant.Error -> UniHubTheme.colorScheme.error.copy(alpha = 0.1f)
    }

    val contentColor = when (variant) {
        UniHubAlertVariant.Info -> UniHubTheme.colorScheme.info
        UniHubAlertVariant.Success -> UniHubTheme.colorScheme.success
        UniHubAlertVariant.Warning -> UniHubTheme.colorScheme.warning
        UniHubAlertVariant.Error -> UniHubTheme.colorScheme.error
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(UniHubTheme.shape.lg)
            .background(backgroundColor)
            .padding(UniHubTheme.spacing.md)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
            Column {
                Text(
                    text = title,
                    style = UniHubTheme.typography.h4,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                Text(
                    text = message,
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.textSecondary
                )
                if (action != null) {
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                    action()
                }
            }
        }
    }
}
