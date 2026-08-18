package com.unihub.app.core.designsystem.component.academic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun UniHubSubjectCard(
    name: String,
    professor: String?,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    UniHubCard(
        modifier = modifier.fillMaxWidth(),
        padding = 0.dp,
        onClick = onClick
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(color)
            )
            
            Column(
                modifier = Modifier
                    .padding(UniHubTheme.spacing.md)
                    .weight(1f)
            ) {
                Text(
                    text = name,
                    style = UniHubTheme.typography.h4,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                
                professor?.let {
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.xxs))
                    Text(
                        text = it,
                        style = UniHubTheme.typography.bodySmall,
                        color = UniHubTheme.colorScheme.textSecondary
                    )
                }
            }
        }
    }
}
