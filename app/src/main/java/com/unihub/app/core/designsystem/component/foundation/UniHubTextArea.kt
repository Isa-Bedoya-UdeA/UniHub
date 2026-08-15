package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            focusedBorderColor = UniHubTheme.colorScheme.primary,
            unfocusedBorderColor = UniHubTheme.colorScheme.border,
            errorBorderColor = UniHubTheme.colorScheme.error,
            focusedLabelColor = UniHubTheme.colorScheme.primary,
            unfocusedLabelColor = UniHubTheme.colorScheme.textSecondary,
            cursorColor = UniHubTheme.colorScheme.primary
        ),
        textStyle = UniHubTheme.typography.body
    )
}
