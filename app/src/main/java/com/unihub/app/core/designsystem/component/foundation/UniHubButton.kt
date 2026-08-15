package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

enum class UniHubButtonVariant {
    Primary,
    Secondary,
    Outlined,
    Text,
    Destructive
}

@Composable
fun UniHubButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: UniHubButtonVariant = UniHubButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    text: String
) {
    val content: @Composable RowScope.() -> Unit = {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp).padding(end = 8.dp)
            )
        }
        Text(
            text = text,
            style = UniHubTheme.typography.label
        )
    }

    val buttonModifier = modifier.height(48.dp)

    when (variant) {
        UniHubButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = UniHubTheme.shape.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UniHubTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                content = content
            )
        }
        UniHubButtonVariant.Secondary -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = UniHubTheme.shape.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UniHubTheme.colorScheme.secondary,
                    contentColor = Color.White
                ),
                content = content
            )
        }
        UniHubButtonVariant.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = UniHubTheme.shape.button,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = UniHubTheme.colorScheme.primary
                ),
                content = content
            )
        }
        UniHubButtonVariant.Text -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = UniHubTheme.shape.button,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = UniHubTheme.colorScheme.primary
                ),
                content = content
            )
        }
        UniHubButtonVariant.Destructive -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = UniHubTheme.shape.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UniHubTheme.colorScheme.error,
                    contentColor = Color.White
                ),
                content = content
            )
        }
    }
}
