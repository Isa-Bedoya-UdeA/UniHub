package com.unihub.app.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.feedback.UniHubLoadingState
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun TestScreen() {
    UniHubTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = UniHubTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(UniHubTheme.spacing.md)
                    .fillMaxSize()
            ) {
                Text(
                    text = "UniHub Design System Test",
                    style = UniHubTheme.typography.h1,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                Text(
                    text = "This is a body text using Inter font.",
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textSecondary
                )
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
                
                Text(
                    text = "Loading Component:",
                    style = UniHubTheme.typography.h3,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                
                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
                
                Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    UniHubLoadingState(modifier = Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

                Text(
                    text = "Button Component:",
                    style = UniHubTheme.typography.h3,
                    color = UniHubTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

                UniHubButton(
                    text = "Primary Button",
                    onClick = { /* Test */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestScreenPreview() {
    TestScreen()
}
