package com.unihub.app.core.designsystem.component.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.component.foundation.UniHubCircularProgress
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubLoadingState(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UniHubCircularProgress(
            modifier = Modifier.size(64.dp)
        )
        
        if (message != null) {
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.lg))
            
            Text(
                text = message,
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UniHubLoadingSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(UniHubTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm)
    ) {
        repeat(lines) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                if (index % 2 == 0) {
                    // Short line
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .width(120.dp)
                            .background(
                                color = UniHubTheme.colorScheme.border.copy(alpha = alpha),
                                shape = UniHubTheme.shape.sm
                            )
                    )
                } else {
                    // Long line
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.7f)
                            .background(
                                color = UniHubTheme.colorScheme.border.copy(alpha = alpha),
                                shape = UniHubTheme.shape.sm
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun UniHubLoadingCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardSkeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardSkeletonAlpha"
    )
    
    UniHubCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(UniHubTheme.spacing.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UniHubTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)
        ) {
            // Title placeholder
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.6f)
                    .background(
                        color = UniHubTheme.colorScheme.border.copy(alpha = alpha),
                        shape = UniHubTheme.shape.sm
                    )
            )
            
            // Subtitle placeholder
            Spacer(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.4f)
                    .background(
                        color = UniHubTheme.colorScheme.border.copy(alpha = alpha),
                        shape = UniHubTheme.shape.sm
                    )
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            
            // Content lines
            repeat(2) {
                Spacer(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth()
                        .background(
                            color = UniHubTheme.colorScheme.border.copy(alpha = alpha),
                            shape = UniHubTheme.shape.sm
                        )
                )
            }
        }
    }
}
