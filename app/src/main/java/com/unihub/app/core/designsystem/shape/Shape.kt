package com.unihub.app.core.designsystem.shape

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class UniHubShape(
    val none: CornerBasedShape = RoundedCornerShape(0.dp),
    val sm: CornerBasedShape = RoundedCornerShape(8.dp),
    val md: CornerBasedShape = RoundedCornerShape(12.dp),
    val lg: CornerBasedShape = RoundedCornerShape(16.dp),
    val xl: CornerBasedShape = RoundedCornerShape(20.dp),
    val twoXl: CornerBasedShape = RoundedCornerShape(24.dp),
    val full: CornerBasedShape = RoundedCornerShape(999.dp),

    // Component specific shapes
    val button: CornerBasedShape = RoundedCornerShape(16.dp),
    val input: CornerBasedShape = RoundedCornerShape(16.dp),
    val card: CornerBasedShape = RoundedCornerShape(20.dp),
    val dialog: CornerBasedShape = RoundedCornerShape(28.dp),
    val bottomSheet: CornerBasedShape = RoundedCornerShape(28.dp),
    val chip: CornerBasedShape = RoundedCornerShape(16.dp),
    val calendarDay: CornerBasedShape = RoundedCornerShape(24.dp),
    val event: CornerBasedShape = RoundedCornerShape(12.dp)
)

val LocalUniHubShape = staticCompositionLocalOf { UniHubShape() }
