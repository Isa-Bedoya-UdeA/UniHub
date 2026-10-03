package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.core.navigation.Screen

sealed class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(Screen.Dashboard, Icons.Default.Dashboard, "Inicio")
    object Calendar : BottomNavItem(Screen.Calendar, Icons.Default.CalendarMonth, "Agenda")
    object Academic : BottomNavItem(Screen.Academic, Icons.Default.School, "Academico")
    object Tasks : BottomNavItem(Screen.Tasks, Icons.AutoMirrored.Filled.List, "Tareas")
    object AiChat : BottomNavItem(Screen.AiChat, Icons.Default.SmartToy, "Asistente")
}

@Composable
fun UniHubBottomNavigation(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Calendar,
        BottomNavItem.Academic,
        BottomNavItem.Tasks,
        BottomNavItem.AiChat
    )

    NavigationBar(
        modifier = modifier,
        containerColor = UniHubTheme.colorScheme.surface,
        tonalElevation = UniHubTheme.spacing.xxs
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = UniHubTheme.typography.label
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = UniHubTheme.colorScheme.primary,
                    selectedTextColor = UniHubTheme.colorScheme.primary,
                    unselectedIconColor = UniHubTheme.colorScheme.textSecondary,
                    unselectedTextColor = UniHubTheme.colorScheme.textSecondary,
                    indicatorColor = UniHubTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
