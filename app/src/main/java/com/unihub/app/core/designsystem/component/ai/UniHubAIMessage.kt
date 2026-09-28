package com.unihub.app.core.designsystem.component.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiMessageRole

@Composable
fun UniHubAIMessage(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == AiMessageRole.USER
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isUser) UniHubTheme.colorScheme.primary else UniHubTheme.colorScheme.cards
    val textColor = if (isUser) androidx.compose.ui.graphics.Color.White else UniHubTheme.colorScheme.textPrimary

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(UniHubTheme.spacing.md)
        ) {
            Text(
                text = message.content,
                style = UniHubTheme.typography.body,
                color = textColor
            )
        }
    }
}
