package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.unihub.app.ui.theme.UniHubTheme

@Composable
fun UniHubProgressBar(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = UniHubTheme.colorScheme.primary,
    trackColor: Color = UniHubTheme.colorScheme.border
) {
    if (progress != null) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round
        )
    }
}
