package com.unihub.app.core.designsystem.component.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.domain.model.AiResponseSource

@Composable
fun UniHubAIMessage(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    when (message.role) {
        AiMessageRole.USER -> UserMessageBubble(message = message, modifier = modifier)
        AiMessageRole.ASSISTANT -> AssistantMessageBubble(message = message, modifier = modifier)
        AiMessageRole.SYSTEM -> SystemMessageBubble(message = message, modifier = modifier)
        AiMessageRole.ERROR -> ErrorMessageBubble(message = message, modifier = modifier)
    }
}

@Composable
private fun UserMessageBubble(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 4.dp
                    )
                )
                .background(UniHubTheme.colorScheme.primary)
                .padding(UniHubTheme.spacing.md)
        ) {
            Text(
                text = message.content,
                style = UniHubTheme.typography.body,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AssistantMessageBubble(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 320.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Asistente",
                    tint = UniHubTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))

            Column {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .background(UniHubTheme.colorScheme.cards)
                        .padding(UniHubTheme.spacing.md)
                ) {
                    Text(
                        text = com.unihub.app.core.util.MarkdownUtils.parseMarkdown(message.content),
                        style = UniHubTheme.typography.body,
                        color = UniHubTheme.colorScheme.textPrimary
                    )
                }

                if (message.source == AiResponseSource.LOCAL_FALLBACK) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⚡ Respuesta local UniHub",
                        style = UniHubTheme.typography.label,
                        color = UniHubTheme.colorScheme.textDisabled,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemMessageBubble(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 320.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(UniHubTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información del sistema",
                    tint = UniHubTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(UniHubTheme.colorScheme.cards)
                    .border(
                        width = 1.dp,
                        color = UniHubTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                    )
                    .padding(UniHubTheme.spacing.md)
            ) {
                Text(
                    text = message.content,
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textPrimary
                )
            }
        }
    }
}

@Composable
private fun ErrorMessageBubble(
    message: AiMessage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 320.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(UniHubTheme.colorScheme.error.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = UniHubTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(UniHubTheme.colorScheme.cards)
                    .border(
                        width = 1.dp,
                        color = UniHubTheme.colorScheme.error.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                    )
                    .padding(UniHubTheme.spacing.md)
            ) {
                Text(
                    text = message.content,
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.error
                )
            }
        }
    }
}
