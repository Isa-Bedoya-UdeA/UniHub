package com.unihub.app.core.designsystem.component.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.ai.domain.model.AiActionType
import com.unihub.app.features.ai.domain.model.AiMessage
import com.unihub.app.features.ai.domain.model.AiMessageRole
import com.unihub.app.features.ai.domain.model.AiPendingAction
import com.unihub.app.features.ai.domain.model.AiResponseSource

@Composable
fun UniHubAIMessage(
    message: AiMessage,
    modifier: Modifier = Modifier,
    onConfirmAction: ((AiPendingAction) -> Unit)? = null,
    onCancelAction: (() -> Unit)? = null,
    isActionActive: Boolean = true
) {
    when (message.role) {
        AiMessageRole.USER -> UserMessageBubble(message = message, modifier = modifier)
        AiMessageRole.ASSISTANT -> AssistantMessageBubble(
            message = message,
            modifier = modifier,
            onConfirmAction = onConfirmAction,
            onCancelAction = onCancelAction,
            isActionActive = isActionActive
        )
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
    modifier: Modifier = Modifier,
    onConfirmAction: ((AiPendingAction) -> Unit)? = null,
    onCancelAction: (() -> Unit)? = null,
    isActionActive: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 340.dp),
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
                    Column {
                        Text(
                            text = com.unihub.app.core.util.MarkdownUtils.parseMarkdown(message.content),
                            style = UniHubTheme.typography.body,
                            color = UniHubTheme.colorScheme.textPrimary
                        )

                        // If message has structured items to confirm
                        val pending = message.pendingAction
                        val items = message.structuredResponse?.items
                        if (pending != null && !items.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                            ConfirmationCard(
                                pendingAction = pending,
                                isActionActive = isActionActive,
                                onConfirm = { onConfirmAction?.invoke(pending) },
                                onCancel = { onCancelAction?.invoke() }
                            )
                        }
                    }
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
private fun ConfirmationCard(
    pendingAction: AiPendingAction,
    isActionActive: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val structured = pendingAction.structuredResponse
    val actionName = when (structured.type) {
        AiActionType.CREATE_SUBJECT -> "Crear materia(s)"
        AiActionType.CREATE_TASK -> "Crear tarea(s)"
        AiActionType.CREATE_EVENT -> "Crear evento(s)"
        AiActionType.REGISTER_GRADE -> "Registrar nota(s)"
        else -> "Confirmar acción"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = UniHubTheme.colorScheme.primary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(UniHubTheme.spacing.sm)
    ) {
        Text(
            text = "📋 $actionName:",
            style = UniHubTheme.typography.h3,
            color = UniHubTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))

        structured.items.forEach { item ->
            val titleStr = item.name ?: item.title ?: "Elemento"
            val detailStr = when (structured.type) {
                AiActionType.CREATE_SUBJECT -> listOfNotNull(item.code, item.professor?.let { "Prof. $it" }, item.credits?.let { "$it cr" }).joinToString(" | ")
                AiActionType.CREATE_TASK -> listOfNotNull(item.subjectName, item.dueAt?.let { "Vence: $it" }, item.priority?.let { "Prioridad: $it" }).joinToString(" | ")
                AiActionType.CREATE_EVENT -> listOfNotNull(item.subjectName, item.startAt?.let { "Inicio: $it" }, item.locationType).joinToString(" | ")
                AiActionType.REGISTER_GRADE -> listOfNotNull(item.subjectName, item.value?.let { "Nota: $it" }, item.weight?.let { "Peso: ${it}%" }).joinToString(" | ")
                else -> ""
            }

            Text(
                text = "• $titleStr${if (detailStr.isNotBlank()) " ($detailStr)" else ""}",
                style = UniHubTheme.typography.bodySmall,
                color = UniHubTheme.colorScheme.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

        if (isActionActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Cancelar", style = UniHubTheme.typography.label)
                }

                Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UniHubTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Confirmar", style = UniHubTheme.typography.label)
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
            modifier = Modifier.widthIn(max = 340.dp),
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
                    text = com.unihub.app.core.util.MarkdownUtils.parseMarkdown(message.content),
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
            modifier = Modifier.widthIn(max = 340.dp),
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
