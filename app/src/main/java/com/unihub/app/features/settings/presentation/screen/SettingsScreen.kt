package com.unihub.app.features.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.core.util.PreferencesManager
import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onNavigateToManageStudies: () -> Unit,
    onNavigateToPermissionGuide: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

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
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = UniHubTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(UniHubTheme.spacing.md)
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

        // Academic Programs
        Text(
            text = "Carrera y Programas",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubCard(padding = 0.dp) {
            SettingsItem(
                icon = Icons.Default.School,
                title = "Programas Académicos",
                onClick = onNavigateToManageStudies,
                isLast = true
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Appearance
        Text(
            text = "Apariencia",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubCard(padding = 0.dp) {
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Tema de la aplicación",
                onClick = { showThemeDialog = true },
                isLast = true
            )
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Account & Security
        Text(
            text = "Seguridad",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubCard(padding = 0.dp) {
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
            text = "Preferencias",
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

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Developer Options
        Text(
            text = "Opciones de Desarrollador",
            style = UniHubTheme.typography.label,
            color = UniHubTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xs))
        UniHubCard(padding = 0.dp) {
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Resetear guía de permisos",
                onClick = {
                    PreferencesManager.setPermissionGuideShown(context, false)
                    onNavigateToPermissionGuide()
                },
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

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Seleccionar Tema") },
            text = {
                Column {
                    ThemeOption(
                        label = "Claro",
                        selected = state.userPreferences?.themeMode == ThemeMode.LIGHT,
                        onClick = {
                            viewModel.updateThemeMode(ThemeMode.LIGHT)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        label = "Oscuro",
                        selected = state.userPreferences?.themeMode == ThemeMode.DARK,
                        onClick = {
                            viewModel.updateThemeMode(ThemeMode.DARK)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        label = "Sistema",
                        selected = state.userPreferences?.themeMode == ThemeMode.SYSTEM,
                        onClick = {
                            viewModel.updateThemeMode(ThemeMode.SYSTEM)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = UniHubTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(UniHubTheme.spacing.sm))
        Text(text = label, style = UniHubTheme.typography.body)
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
