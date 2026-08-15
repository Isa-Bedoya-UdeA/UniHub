package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.unihub.app.core.designsystem.component.foundation.UniHubCircularProgress

@Composable
fun UniHubLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        UniHubCircularProgress()
    }
}
