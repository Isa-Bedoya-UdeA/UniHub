package com.unihub.app.core.designsystem.component.foundation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
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
    object Subjects : BottomNavItem(Screen.Subjects, Icons.AutoMirrored.Filled.List, "Materias")
    object Academic : BottomNavItem(Screen.Academic, Icons.Default.School, "Académico")
    object Settings : BottomNavItem(Screen.Settings, Icons.Default.Settings, "Ajustes")
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
        BottomNavItem.Subjects,
        BottomNavItem.Academic,
        BottomNavItem.Settings
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
