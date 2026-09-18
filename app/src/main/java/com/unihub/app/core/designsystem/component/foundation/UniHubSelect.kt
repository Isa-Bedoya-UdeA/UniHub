package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unihub.app.core.designsystem.theme.UniHubTheme

data class SelectOption<T>(
    val value: T,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
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

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        UniHubTextField(
            value = selectedLabel,
            onValueChange = {},
            label = label,
            placeholder = placeholder,
            isError = isError,
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
