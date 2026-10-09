package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unihub.app.core.designsystem.component.foundation.UniHubConfirmationDialog as FoundationConfirmationDialog

@Composable
fun UniHubConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    dismissText: String = "Cancelar",
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    FoundationConfirmationDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm,
        title = title,
        message = message,
        confirmText = confirmText,
        dismissText = dismissText,
        modifier = modifier,
        isDestructive = isDestructive
    )
}
