package com.unihub.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Map UniHub tokens to MaterialTheme if needed, or just provide them directly
    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colorScheme.primary,
            secondary = colorScheme.secondary,
            background = colorScheme.background,
            surface = colorScheme.surface,
            error = colorScheme.error
        )
    } else {
        lightColorScheme(
            primary = colorScheme.primary,
            secondary = colorScheme.secondary,
            background = colorScheme.background,
            surface = colorScheme.surface,
            error = colorScheme.error
        )
    }

    CompositionLocalProvider(
        LocalUniHubColorScheme provides colorScheme,
        LocalUniHubTypography provides UniHubTypography(),
        LocalUniHubSpacing provides UniHubSpacing(),
        LocalUniHubShape provides UniHubShape()
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            // We can also map our typography to Material3 Typography if we want
            // to use Material3 components that rely on it.
            typography = Typography(), 
            content = content
        )
    }
}
