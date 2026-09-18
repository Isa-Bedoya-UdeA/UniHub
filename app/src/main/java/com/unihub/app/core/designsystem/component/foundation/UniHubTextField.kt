package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: () -> Unit = {},
    forceDarkText: Boolean = false,
    singleLine: Boolean = true
) {
    val textColor = if (forceDarkText) Color.Black else UniHubTheme.colorScheme.textPrimary
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
        singleLine = singleLine,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        trailingIcon = trailingIcon,
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                            if (readOnly && enabled) onImeAction()
                        }
                    }
                }
            },
        shape = UniHubTheme.shape.input,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            disabledTextColor = textColor,
            errorTextColor = textColor,
            focusedContainerColor = if (forceDarkText) Color.White else Color.Transparent,
            unfocusedContainerColor = if (forceDarkText) Color.White else Color.Transparent,
            disabledContainerColor = if (forceDarkText) Color.White else Color.Transparent,
            errorContainerColor = if (forceDarkText) Color.White else Color.Transparent,
            cursorColor = if (forceDarkText) Color.Black else UniHubTheme.colorScheme.primary,
            errorCursorColor = UniHubTheme.colorScheme.error,
            focusedBorderColor = UniHubTheme.colorScheme.primary,
            unfocusedBorderColor = UniHubTheme.colorScheme.border,
            disabledBorderColor = UniHubTheme.colorScheme.border,
            errorBorderColor = UniHubTheme.colorScheme.error,
            focusedLabelColor = UniHubTheme.colorScheme.primary,
            unfocusedLabelColor = UniHubTheme.colorScheme.textSecondary,
            disabledLabelColor = UniHubTheme.colorScheme.textSecondary,
            errorLabelColor = UniHubTheme.colorScheme.error,
            focusedPlaceholderColor = UniHubTheme.colorScheme.textSecondary,
            unfocusedPlaceholderColor = UniHubTheme.colorScheme.textSecondary,
            disabledPlaceholderColor = UniHubTheme.colorScheme.textSecondary,
            errorPlaceholderColor = UniHubTheme.colorScheme.textSecondary,
            focusedLeadingIconColor = UniHubTheme.colorScheme.textSecondary,
            unfocusedLeadingIconColor = UniHubTheme.colorScheme.textSecondary,
            disabledLeadingIconColor = UniHubTheme.colorScheme.textSecondary,
            errorLeadingIconColor = UniHubTheme.colorScheme.textSecondary,
            focusedTrailingIconColor = UniHubTheme.colorScheme.textSecondary,
            unfocusedTrailingIconColor = UniHubTheme.colorScheme.textSecondary,
            disabledTrailingIconColor = UniHubTheme.colorScheme.textSecondary,
            errorTrailingIconColor = UniHubTheme.colorScheme.textSecondary,
        ),
        textStyle = UniHubTheme.typography.body,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onSearch = { onImeAction() },
            onDone = { onImeAction() },
            onGo = { onImeAction() }
        )
    )
}
