package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubCircularProgress(
    modifier: Modifier = Modifier,
    color: Color = UniHubTheme.colorScheme.primary,
    progress: Float? = null
) {
    if (progress != null) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = color,
            trackColor = UniHubTheme.colorScheme.border
        )
    } else {
        CircularProgressIndicator(
            modifier = modifier,
            color = color
        )
    }
}
