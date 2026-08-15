package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    selected: Boolean = false
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = UniHubTheme.typography.label
            )
        },
        modifier = modifier.height(32.dp),
        enabled = enabled,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        shape = UniHubTheme.shape.chip,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.cards,
            labelColor = if (selected) UniHubTheme.colorScheme.surface else UniHubTheme.colorScheme.textPrimary,
            leadingIconContentColor = if (selected) UniHubTheme.colorScheme.surface else UniHubTheme.colorScheme.primary
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = enabled,
            borderColor = UniHubTheme.colorScheme.border
        )
    )
}
