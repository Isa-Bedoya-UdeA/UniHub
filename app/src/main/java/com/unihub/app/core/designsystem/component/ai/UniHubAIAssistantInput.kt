package com.unihub.app.core.designsystem.component.ai

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubAIAssistantInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "Pregúntale al asistente UniHub..."
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(UniHubTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
    ) {
        UniHubTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = placeholder,
            enabled = enabled,
            singleLine = true,
            imeAction = androidx.compose.ui.text.input.ImeAction.Send,
            onImeAction = onSend
        )

        IconButton(
            onClick = onSend,
            enabled = enabled && value.isNotBlank(),
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = UniHubTheme.colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = UniHubTheme.colorScheme.border,
                disabledContentColor = UniHubTheme.colorScheme.textDisabled
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar"
            )
        }
    }
}
