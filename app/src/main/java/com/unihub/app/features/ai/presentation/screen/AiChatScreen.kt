package com.unihub.app.features.ai.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.ai.UniHubAIAssistantInput
import com.unihub.app.core.designsystem.component.ai.UniHubAIMessage
import com.unihub.app.core.designsystem.component.foundation.UniHubCircularProgress
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.features.ai.presentation.viewmodel.AiChatViewModel

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar with Assistant title and New Chat button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UniHubTheme.spacing.md, vertical = UniHubTheme.spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Asistente AI",
                            tint = UniHubTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
                    Text(
                        text = "Asistente UniHub",
                        style = UniHubTheme.typography.h3,
                        color = UniHubTheme.colorScheme.textPrimary
                    )
                }

                if (state.messages.isNotEmpty()) {
                    androidx.compose.material3.FilledTonalIconButton(
                        onClick = { viewModel.newChat() },
                        modifier = Modifier.size(36.dp),
                        colors = androidx.compose.material3.IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = UniHubTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            contentColor = UniHubTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nuevo chat",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = UniHubTheme.spacing.md),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.sm),
                contentPadding = PaddingValues(vertical = UniHubTheme.spacing.md)
            ) {
                if (state.messages.isEmpty() && !state.isLoading) {
                    item {
                        WelcomeCard(userName = state.userName)
                    }
                }

                items(state.messages) { message ->
                    UniHubAIMessage(message = message)
                }

                if (state.isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(UniHubTheme.spacing.sm),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = UniHubTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = UniHubTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                            UniHubCircularProgress(
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
                            Text(
                                text = "Pensando...",
                                style = UniHubTheme.typography.bodySmall,
                                color = UniHubTheme.colorScheme.textSecondary
                            )
                        }
                    }
                }
            }

            if (state.isLimitedMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UniHubTheme.spacing.md, vertical = UniHubTheme.spacing.xs)
                        .clip(RoundedCornerShape(8.dp))
                        .background(UniHubTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        .padding(horizontal = UniHubTheme.spacing.sm, vertical = UniHubTheme.spacing.xs)
                ) {
                    Text(
                        text = "ℹ️ Modo limitado: el asistente externo no está disponible. Puedes consultar eventos, tareas y plan del día.",
                        style = UniHubTheme.typography.label,
                        color = UniHubTheme.colorScheme.secondary
                    )
                }
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = UniHubTheme.spacing.md)
                )
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
            }

            UniHubAIAssistantInput(
                value = state.inputText,
                onValueChange = viewModel::onInputChanged,
                onSend = viewModel::sendMessage,
                enabled = !state.isLoading
            )
        }
    }
}

@Composable
private fun WelcomeCard(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = UniHubTheme.spacing.lg),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.5.dp,
                    color = UniHubTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(UniHubTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = UniHubTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Asistente UniHub",
                    tint = UniHubTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

            Text(
                text = "Hola, $userName",
                style = UniHubTheme.typography.h2,
                color = UniHubTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

            Text(
                text = "Soy tu asistente de IA. Puedo ayudarte a organizar tu horario, hacer seguimiento de tareas y optimizar tu tiempo de estudio. Como puedo ayudarte hoy?",
                style = UniHubTheme.typography.body,
                color = UniHubTheme.colorScheme.textSecondary,
                modifier = Modifier.padding(horizontal = UniHubTheme.spacing.md)
            )
        }
    }
}
