package com.unihub.app.features.events.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.unihub.app.core.designsystem.component.academic.UniHubLocationCard
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubCard
import com.unihub.app.core.designsystem.theme.UniHubTheme

@Composable
fun EventDetailsScreen(
    eventId: String,
    onBack: () -> Unit
) {
    var remindMe by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(UniHubTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(UniHubTheme.spacing.xs)) {
                TypeBadge(text = "COMP-204", color = UniHubTheme.colorScheme.primary)
                TypeBadge(text = "Clase", color = UniHubTheme.colorScheme.secondary)
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
            
            Text(text = "Computación Móvil", style = UniHubTheme.typography.h1)
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            // DateTime Card
            UniHubCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccessTime, null, tint = UniHubTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                    Column {
                        Text(text = "Jueves, 24 Oct", style = UniHubTheme.typography.h4)
                        Text(text = "18:00 - 20:00", style = UniHubTheme.typography.body, color = UniHubTheme.colorScheme.textSecondary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Location Card
            UniHubLocationCard(
                place = "UdeA",
                room = "204",
                building = "Bloque de Ingeniería",
                onOpenInMaps = {}
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Professor/Subject Details Card
            UniHubCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(UniHubTheme.colorScheme.border),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = UniHubTheme.colorScheme.textSecondary)
                        }
                        Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                        Column {
                            Text(text = "Dr. Elena Rodriguez", style = UniHubTheme.typography.h4)
                            Text(text = "Profesor • Detalles de Materia", style = UniHubTheme.typography.bodySmall, color = UniHubTheme.colorScheme.textSecondary)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(16.dp), tint = UniHubTheme.colorScheme.textDisabled)
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Notes Card
            UniHubCard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.StickyNote2, null, tint = UniHubTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(UniHubTheme.spacing.xs))
                        Text(text = "Notas", style = UniHubTheme.typography.label)
                    }
                    Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))
                    Text(
                        text = "Trae tu laptop para el laboratorio de Jetpack Compose y Hilt. Asegúrate de tener la última versión de Android Studio instalada.",
                        style = UniHubTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))
            
            // Reminder Switch
            UniHubCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, null, tint = UniHubTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(UniHubTheme.spacing.md))
                        Text(text = "Recordarme 15 mins antes", style = UniHubTheme.typography.body)
                    }
                    Switch(checked = remindMe, onCheckedChange = { remindMe = it })
                }
            }
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
            
            UniHubButton(
                text = "Editar Evento",
                variant = UniHubButtonVariant.Outlined,
                onClick = { },
                leadingIcon = Icons.Default.Edit,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))
        }
    }
}

@Composable
fun TypeBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(UniHubTheme.shape.sm)
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = UniHubTheme.typography.label, color = color)
    }
}
