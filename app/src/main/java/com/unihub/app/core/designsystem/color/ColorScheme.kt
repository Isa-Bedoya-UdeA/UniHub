package com.unihub.app.core.designsystem.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class UniHubColorScheme(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val cards: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val info: Color,
    val success: Color,
    val warning: Color,
    val error: Color
)

val LightColorScheme = UniHubColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    accent = LightAccent,
    background = LightBackground,
    surface = LightSurface,
    cards = LightCards,
    border = LightBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textDisabled = LightTextDisabled,
    info = Info,
    success = Success,
    warning = Warning,
    error = Error
)

val DarkColorScheme = UniHubColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    accent = DarkAccent,
    background = DarkBackground,
    surface = DarkSurface,
    cards = DarkCards,
    border = DarkBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textDisabled = DarkTextDisabled,
    info = Info,
    success = Success,
    warning = Warning,
    error = Error
)

val LocalUniHubColorScheme = staticCompositionLocalOf { LightColorScheme }
