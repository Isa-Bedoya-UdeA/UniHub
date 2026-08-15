package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.unihub.app.ui.theme.UniHubTheme

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
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
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
        shape = UniHubTheme.shape.input,
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
