package com.unihub.app.features.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(UniHubTheme.spacing.md)
    ) {
        Text(
            text = "Ajustes",
            style = UniHubTheme.typography.h1,
            color = UniHubTheme.colorScheme.textPrimary
        )
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Profile Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(UniHubTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JD",
                    style = UniHubTheme.typography.h2,
                    color = UniHubTheme.colorScheme.surface
                )
            }
            
            Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
            
            Column {
                Text(
                    text = "Juan Doe",
                    style = UniHubTheme.typography.h3,
                    color = UniHubTheme.colorScheme.textPrimary
                )
                Text(
                    text = "juan.doe@udea.edu.co",
                    style = UniHubTheme.typography.bodySmall,
                    color = UniHubTheme.colorScheme.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

        UniHubButton(
            text = "Editar Perfil",
            variant = UniHubButtonVariant.Outlined,
            onClick = { /* Edit */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.twoXl))

        // Account & Security
        Text(
            text = "Cuenta y Seguridad",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubCard(padding = 0.dp) {
            SettingsItem(
                icon = Icons.Default.School,
                title = "Información de la universidad",
                onClick = {}
            )
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Cambiar contraseña",
                onClick = {}
            )
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notificaciones",
                onClick = {},
                isLast = true
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Academic Preferences
        Text(
            text = "Preferencias Académicas",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubCard(padding = 0.dp) {
            SettingsItem(
                icon = Icons.Default.ColorLens,
                title = "Colores de las materias",
                onClick = {},
                isLast = true
            )
        }
        
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.threeXl))
        
        UniHubButton(
            text = "Cerrar Sesión",
            variant = UniHubButtonVariant.Destructive,
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UniHubTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = UniHubTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                Text(
                    text = title,
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textPrimary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = UniHubTheme.colorScheme.textDisabled,
                modifier = Modifier.size(16.dp)
            )
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = UniHubTheme.colorScheme.border
            )
        }
    }
}
