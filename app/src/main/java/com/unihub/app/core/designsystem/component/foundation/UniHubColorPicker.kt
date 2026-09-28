package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

data class ColorOption(
    val hex: String,
    val color: Color
)

val PRESET_COLORS = listOf(
    ColorOption("#4F46E5", Color(0xFF4F46E5)),
    ColorOption("#7C3AED", Color(0xFF7C3AED)),
    ColorOption("#06B6D4", Color(0xFF06B6D4)),
    ColorOption("#65CD9B", Color(0xFF65CD9B)),
    ColorOption("#EBB55D", Color(0xFFEBB55D)),
    ColorOption("#F98087", Color(0xFFF98087)),
    ColorOption("#76ABEB", Color(0xFF76ABEB)),
    ColorOption("#F472B6", Color(0xFFF472B6)),
    ColorOption("#FB923C", Color(0xFFFB923C)),
    ColorOption("#A3E635", Color(0xFFA3E635)),
    ColorOption("#2DD4BF", Color(0xFF2DD4BF)),
    ColorOption("#818CF8", Color(0xFF818CF8)),
    ColorOption("#E879F9", Color(0xFFE879F9)),
    ColorOption("#F87171", Color(0xFFF87171)),
    ColorOption("#FBBF24", Color(0xFFFBBF24)),
    ColorOption("#34D399", Color(0xFF34D399)),
    ColorOption("#60A5FA", Color(0xFF60A5FA)),
    ColorOption("#A78BFA", Color(0xFFA78BFA)),
    ColorOption("#F9A8D4", Color(0xFFF9A8D4)),
    ColorOption("#FCA5A5", Color(0xFFFCA5A5)),
)

@Composable
fun UniHubColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Color de la materia"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        try { Color(android.graphics.Color.parseColor(selectedColor)) }
                        catch (_: Exception) { Color(0xFF4F46E5) }
                    )
                    .border(
                        width = 2.dp,
                        color = UniHubTheme.colorScheme.border,
                        shape = CircleShape
                    )
            )

            Text(
                text = selectedColor.uppercase(),
                style = UniHubTheme.typography.bodySmall,
                color = UniHubTheme.colorScheme.textPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = UniHubTheme.colorScheme.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(PRESET_COLORS) { colorOption ->
                val isSelected = selectedColor.equals(colorOption.hex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colorOption.color)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) UniHubTheme.colorScheme.textPrimary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(colorOption.hex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
