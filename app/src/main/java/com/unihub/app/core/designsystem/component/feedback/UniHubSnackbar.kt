package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.common.state.MessageType
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubSnackbar(
    message: String,
    messageType: MessageType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (messageType) {
        MessageType.SUCCESS -> UniHubTheme.colorScheme.success
        MessageType.ERROR -> UniHubTheme.colorScheme.error
        MessageType.WARNING -> UniHubTheme.colorScheme.warning
        MessageType.INFO -> UniHubTheme.colorScheme.info
    }
    
    val icon = when (messageType) {
        MessageType.SUCCESS -> Icons.Default.CheckCircle
        MessageType.ERROR -> Icons.Default.Error
        MessageType.WARNING -> Icons.Default.Warning
        MessageType.INFO -> Icons.Default.Info
    }
    
    Snackbar(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        containerColor = backgroundColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White,
                style = UniHubTheme.typography.body
            )
        }
    }
}
