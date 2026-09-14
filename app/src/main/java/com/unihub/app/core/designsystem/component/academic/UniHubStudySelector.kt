package com.unihub.app.core.designsystem.component.academic

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.unihub.app.core.designsystem.component.foundation.UniHubTextField
import com.unihub.app.core.designsystem.theme.UniHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniHubStudySelector(
    studies: List<StudyOption>,
    selectedStudyId: String?,
    onStudySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedStudy = studies.find { it.id == selectedStudyId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        UniHubTextField(
            value = selectedStudy?.name ?: "Seleccionar Programa",
            onValueChange = {},
            readOnly = true,
            leadingIcon = Icons.Default.School,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            enabled = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            studies.forEach { study ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(text = study.name, style = UniHubTheme.typography.body)
                            Text(text = study.institution, style = UniHubTheme.typography.label, color = UniHubTheme.colorScheme.textSecondary)
                        }
                    },
                    onClick = {
                        onStudySelected(study.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
