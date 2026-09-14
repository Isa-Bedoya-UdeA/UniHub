package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

data class SelectOption<T>(
    val value: T,
    val label: String
)

@Composable
fun <T> UniHubSelect(
    options: List<SelectOption<T>>,
    selectedValue: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Seleccionar",
    isError: Boolean = false,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    
    val selectedLabel = options.find { it.value == selectedValue }?.label ?: placeholder

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { expanded = true }
        ) {
            UniHubTextField(
                value = selectedLabel,
                onValueChange = {},
                label = label,
                placeholder = placeholder,
                isError = isError,
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            style = UniHubTheme.typography.body,
                            color = if (option.value == selectedValue) {
                                UniHubTheme.colorScheme.primary
                            } else {
                                UniHubTheme.colorScheme.textPrimary
                            }
                        )
                    },
                    onClick = {
                        onOptionSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}
