package com.unihub.app.features.ai.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.ai.UniHubAIAssistantInput
import com.unihub.app.core.designsystem.component.ai.UniHubAIMessage
import com.unihub.app.core.designsystem.component.feedback.UniHubLoadingState
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(UniHubTheme.spacing.twoXl),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Asistente UniHub",
                                style = UniHubTheme.typography.h3,
                                color = UniHubTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                            Text(
                                text = "Pregúntame sobre cómo organizar tu día académico.",
                                style = UniHubTheme.typography.body,
                                color = UniHubTheme.colorScheme.textSecondary
                            )
                        }
                    }
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
                        horizontalArrangement = Arrangement.Start
                    ) {
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
