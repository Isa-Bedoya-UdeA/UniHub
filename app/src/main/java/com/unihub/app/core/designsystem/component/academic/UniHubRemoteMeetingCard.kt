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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubRemoteMeetingCard(
    onJoinMeeting: () -> Unit
) {
    UniHubCard(padding = 0.dp) {
        Column(modifier = Modifier.padding(UniHubTheme.spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(UniHubTheme.colorScheme.accent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoCall,
                        contentDescription = null,
                        tint = UniHubTheme.colorScheme.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                Column {
                    Text(text = "Evento Remoto", style = UniHubTheme.typography.h4)
                    Text(text = "Reunión virtual", style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            UniHubButton(
                text = "Unirse a la reunión",
                onClick = onJoinMeeting,
                leadingIcon = Icons.Default.Link,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
