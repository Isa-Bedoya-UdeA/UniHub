package com.unihub.app.features.auth.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UniHubTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = UniHubTheme.colorScheme.accent,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        
        Text(
            text = "Inicia Sesión",
            style = UniHubTheme.typography.h2,
            color = UniHubTheme.colorScheme.primary // Use primary color
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        Text(
            text = "Usa tu cuenta de Google para acceder a UniHub.",
            style = UniHubTheme.typography.body,
            color = UniHubTheme.colorScheme.textSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.threeXl))
        
        UniHubButton(
            text = "Continuar con Google",
            onClick = onLoginSuccess,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
        
        UniHubButton(
            text = "Entrar como invitado",
            variant = UniHubButtonVariant.Outlined,
            onClick = onLoginSuccess,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
