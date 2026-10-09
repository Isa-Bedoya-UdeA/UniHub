package com.unihub.app.core.designsystem.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Animation utilities for UniHub following Material Design 3 principles.
 * All animations should be:
 * - Purposeful (not decorative)
 * - Smooth and natural
 * - Respecting user accessibility settings
 * - Performant (60fps)
 */

// Animation durations following MD3 guidelines
object AnimationDurations {
    const val SHORT = 150  // Micro-interactions
    const val MEDIUM = 300 // Standard transitions
    const val LONG = 500   // Complex animations
}

// Animation specs for different use cases
object AnimationSpecs {
    val standard = tween<Float>(
        durationMillis = AnimationDurations.MEDIUM,
        easing = FastOutSlowInEasing
    )
    
    val quick = tween<Float>(
        durationMillis = AnimationDurations.SHORT,
        easing = FastOutSlowInEasing
    )
    
    val springy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * Modifier that fades in content with optional scale animation.
 * Useful for cards, lists, and other content appearing on screen.
 */
fun Modifier.fadeIn(
    durationMillis: Int = AnimationDurations.MEDIUM,
    initialAlpha: Float = 0f,
    initialScale: Float = 1f
): Modifier = composed {
    val alpha = remember { Animatable(initialAlpha) }
    val scale = remember { Animatable(initialScale) }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    
    this
        .alpha(alpha.value)
        .scale(scale.value)
}

/**
 * Modifier that animates content appearing from bottom to top.
 * Useful for bottom sheets, dialogs, and floating elements.
 */
fun Modifier.slideUp(
    durationMillis: Int = AnimationDurations.MEDIUM,
    initialOffsetY: Float = 50f
): Modifier = composed {
    val offsetY = remember { Animatable(initialOffsetY) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }
    
    this
        .alpha(alpha.value)
        .graphicsLayer { translationY = offsetY.value }
}

/**
 * Modifier that creates a subtle pulse animation.
 * Useful for drawing attention to important elements.
 */
fun Modifier.pulse(
    enabled: Boolean = true,
    durationMillis: Int = 1500
): Modifier = composed {
    if (!enabled) return@composed this
    
    val alpha = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            alpha.animateTo(
                targetValue = 0.6f,
                animationSpec = tween(durationMillis / 2)
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis / 2)
            )
        }
    }
    
    this.alpha(alpha.value)
}
