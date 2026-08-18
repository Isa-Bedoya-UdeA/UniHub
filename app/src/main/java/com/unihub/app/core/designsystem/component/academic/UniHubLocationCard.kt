package com.unihub.app.core.designsystem.component.academic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubLocationCard(
    place: String,
    room: String,
    building: String,
    onOpenInMaps: () -> Unit
) {
    UniHubCard(padding = 0.dp) {
        Column(modifier = Modifier.padding(UniHubTheme.spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = UniHubTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                Column {
                    Text(text = "$place - $room", style = UniHubTheme.typography.h4)
                    Text(text = building, style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(UniHubTheme.colorScheme.border.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = UniHubTheme.colorScheme.textDisabled,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            UniHubButton(
                text = "Abrir en Maps",
                variant = UniHubButtonVariant.Outlined,
                onClick = onOpenInMaps,
                leadingIcon = Icons.Default.Map,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
