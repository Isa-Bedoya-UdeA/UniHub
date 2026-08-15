package com.unihub.app.core.designsystem.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class UniHubSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val twoXl: Dp = 32.dp,
    val threeXl: Dp = 40.dp,
    val fourXl: Dp = 48.dp,
    val fiveXl: Dp = 64.dp,

    // Layout tokens
    val screenPaddingHorizontal: Dp = 16.dp,
    val screenPaddingVertical: Dp = 24.dp,
    val sectionSpacing: Dp = 24.dp,
    val contentSpacing: Dp = 16.dp,
    val itemSpacing: Dp = 12.dp,

    // Touch targets
    val touchTargetMin: Dp = 48.dp,
    val iconButtonSize: Dp = 48.dp,
    val fabSize: Dp = 56.dp
)

val LocalUniHubSpacing = staticCompositionLocalOf { UniHubSpacing() }
