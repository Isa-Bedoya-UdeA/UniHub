package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = UniHubTheme.shape.dialog,
            color = UniHubTheme.colorScheme.surface,
            contentColor = UniHubTheme.colorScheme.textPrimary
        ) {
            Column(
                modifier = Modifier.padding(UniHubTheme.spacing.xl) // 24dp
            ) {
                content()
            }
        }
    }
}

@Composable
fun UniHubConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    UniHubDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column {
            Text(
                text = title,
                style = UniHubTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            Text(
                text = message,
                style = UniHubTheme.typography.body
            )
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                UniHubButton(
                    onClick = onDismissRequest,
                    text = dismissText,
                    variant = UniHubButtonVariant.Text
                )
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                UniHubButton(
                    onClick = onConfirm,
                    text = confirmText,
                    variant = if (isDestructive) UniHubButtonVariant.Destructive else UniHubButtonVariant.Primary
                )
            }
        }
    }
}
