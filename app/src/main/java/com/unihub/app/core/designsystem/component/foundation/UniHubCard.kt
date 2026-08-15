package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubCard(
    modifier: Modifier = Modifier,
    padding: Dp = UniHubTheme.spacing.md, // 16dp
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = UniHubTheme.shape.card,
        colors = CardDefaults.cardColors(
            containerColor = UniHubTheme.colorScheme.cards,
            contentColor = UniHubTheme.colorScheme.textPrimary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}
