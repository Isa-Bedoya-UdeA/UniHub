package com.unihub.app.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.unihub.app.core.designsystem.color.DarkColorScheme
import com.unihub.app.core.designsystem.color.LightColorScheme
import com.unihub.app.core.designsystem.color.LocalUniHubColorScheme
import com.unihub.app.core.designsystem.color.UniHubColorScheme
import com.unihub.app.core.designsystem.shape.LocalUniHubShape
import com.unihub.app.core.designsystem.shape.UniHubShape
import com.unihub.app.core.designsystem.spacing.LocalUniHubSpacing
import com.unihub.app.core.designsystem.spacing.UniHubSpacing
import com.unihub.app.core.designsystem.typography.LocalUniHubTypography
import com.unihub.app.core.designsystem.typography.UniHubTypography

object UniHubTheme {
    val colorScheme: UniHubColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalUniHubColorScheme.current

    val typography: UniHubTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalUniHubTypography.current

    val spacing: UniHubSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalUniHubSpacing.current

    val shape: UniHubShape
        @Composable
        @ReadOnlyComposable
        get() = LocalUniHubShape.current
}

@Composable
fun UniHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalUniHubColorScheme provides colorScheme,
        LocalUniHubTypography provides UniHubTypography(),
        LocalUniHubSpacing provides UniHubSpacing(),
        LocalUniHubShape provides UniHubShape(),
        content = content
    )
}
