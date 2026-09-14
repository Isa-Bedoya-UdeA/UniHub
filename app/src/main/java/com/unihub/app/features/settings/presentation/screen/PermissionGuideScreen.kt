package com.unihub.app.features.settings.presentation.screen

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.unihub.app.core.designsystem.component.foundation.UniHubAlert
import com.unihub.app.core.designsystem.component.foundation.UniHubAlertVariant
import com.unihub.app.core.designsystem.component.foundation.UniHubButton
import com.unihub.app.core.designsystem.component.foundation.UniHubButtonVariant
import com.unihub.app.core.designsystem.theme.UniHubTheme
import kotlinx.coroutines.launch

data class PermissionStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isGranted: () -> Boolean,
    val onOpenSettings: () -> Unit,
    val settingsLabel: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionGuideScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val notificationPermissionGranted = {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    val exactAlarmPermissionGranted = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    val openNotificationSettings = {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    val openExactAlarmSettings = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    val steps = listOf(
        PermissionStep(
            title = "Notificaciones",
            description = "UniHub necesita enviarte notificaciones para recordarte sobre eventos, tareas y deadlines importantes. Sin este permiso, no podrás recibir alertas.",
            icon = Icons.Default.Notifications,
            isGranted = notificationPermissionGranted,
            onOpenSettings = openNotificationSettings,
            settingsLabel = "Activar Notificaciones"
        ),
        PermissionStep(
            title = "Alarmas y Recordatorios",
            description = "Para programar recordatorios precisos en horarios específicos, UniHub necesita permiso para usar alarmas exactas. Esto es especialmente importante en Android 14 y superior.",
            icon = Icons.Default.Alarm,
            isGranted = exactAlarmPermissionGranted,
            onOpenSettings = openExactAlarmSettings,
            settingsLabel = "Activar Alarmas Exactas"
        ),
        PermissionStep(
            title = "¡Todo Listo!",
            description = "Has configurado todos los permisos necesarios. Ahora UniHub puede enviarte recordatorios precisos y mantenerte organizado.",
            icon = Icons.Default.CheckCircle,
            isGranted = { true },
            onOpenSettings = {},
            settingsLabel = ""
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UniHubTheme.colorScheme.background)
            .padding(UniHubTheme.spacing.xl)
    ) {
        // Header
        Text(
            text = "Configuración de Permisos",
            style = UniHubTheme.typography.h1,
            color = UniHubTheme.colorScheme.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.sm))

        Text(
            text = "Para ofrecerte la mejor experiencia, necesitamos algunos permisos",
            style = UniHubTheme.typography.body,
            color = UniHubTheme.colorScheme.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Page indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val color = if (index == pagerState.currentPage) {
                    UniHubTheme.colorScheme.primary
                } else {
                    UniHubTheme.colorScheme.border
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val step = steps[page]
            val isGranted by remember(step.isGranted()) {
                mutableStateOf(step.isGranted())
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            if (isGranted) {
                                UniHubTheme.colorScheme.success.copy(alpha = 0.1f)
                            } else {
                                UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = if (isGranted) {
                            UniHubTheme.colorScheme.success
                        } else {
                            UniHubTheme.colorScheme.primary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

                // Title
                Text(
                    text = step.title,
                    style = UniHubTheme.typography.h2,
                    color = UniHubTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

                // Description
                Text(
                    text = step.description,
                    style = UniHubTheme.typography.body,
                    color = UniHubTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = UniHubTheme.spacing.md)
                )

                Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

                // Status or Action
                if (page < 2) {
                    if (isGranted) {
                        UniHubAlert(
                            variant = UniHubAlertVariant.Success,
                            title = "Permiso activado",
                            message = "Este permiso ya está configurado correctamente.",
                            icon = Icons.Default.CheckCircle
                        )
                    } else {
                        UniHubButton(
                            text = step.settingsLabel,
                            variant = UniHubButtonVariant.Primary,
                            onClick = {
                                step.onOpenSettings()
                            },
                            leadingIcon = Icons.Default.Settings,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(UniHubTheme.spacing.md))

                        Text(
                            text = "Después de activar el permiso, regresa a la app y presiona 'Verificar'",
                            style = UniHubTheme.typography.bodySmall,
                            color = UniHubTheme.colorScheme.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = UniHubTheme.spacing.md)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(UniHubTheme.spacing.xl))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                UniHubButton(
                    text = "Atrás",
                    variant = UniHubButtonVariant.Text,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (pagerState.currentPage < 2) {
                UniHubButton(
                    text = "Siguiente",
                    variant = UniHubButtonVariant.Primary,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            } else {
                UniHubButton(
                    text = "Comenzar",
                    variant = UniHubButtonVariant.Primary,
                    onClick = onFinished,
                    leadingIcon = Icons.Default.ArrowForward
                )
            }
        }
    }
}
