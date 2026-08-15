package com.unihub.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unihub.app.core.designsystem.component.foundation.UniHubBottomNavigation
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.ui.test.TestScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if current route is a bottom nav destination
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Calendar.route,
        Screen.Subjects.route,
        Screen.Academic.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                UniHubBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = UniHubTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                TestScreen()
            }
            composable(Screen.Calendar.route) {
                PlaceholderScreen("Agenda")
            }
            composable(Screen.Subjects.route) {
                PlaceholderScreen("Materias")
            }
            composable(Screen.Academic.route) {
                PlaceholderScreen("Académico")
            }
            composable(Screen.Settings.route) {
                PlaceholderScreen("Ajustes")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Text(
        text = "$name Screen Placeholder",
        style = UniHubTheme.typography.h2,
        color = UniHubTheme.colorScheme.textPrimary,
        modifier = Modifier.padding(UniHubTheme.spacing.md)
    )
}
