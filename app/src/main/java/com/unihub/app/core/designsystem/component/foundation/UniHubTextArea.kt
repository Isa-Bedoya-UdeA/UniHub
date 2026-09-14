package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    minLines: Int = 3
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
        isError = isError,
        enabled = enabled,
        shape = UniHubTheme.shape.input,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = UniHubTheme.colorScheme.textPrimary,
            unfocusedTextColor = UniHubTheme.colorScheme.textPrimary,
            disabledTextColor = UniHubTheme.colorScheme.textPrimary,
            errorTextColor = UniHubTheme.colorScheme.textPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            cursorColor = UniHubTheme.colorScheme.primary,
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
        ),
        textStyle = UniHubTheme.typography.body
    )
}
