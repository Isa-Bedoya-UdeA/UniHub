package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubSuccessMessage(
    title: String = "¡Operación exitosa!",
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    UniHubCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(UniHubTheme.spacing.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UniHubTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = UniHubTheme.colorScheme.success
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            Text(
                text = title,
                style = UniHubTheme.typography.h3,
                color = UniHubTheme.colorScheme.success,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            
            Text(
                text = message,
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
            
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))
                
                UniHubButton(
                    text = actionLabel,
                    onClick = onAction
                )
            }
        }
    }
}
