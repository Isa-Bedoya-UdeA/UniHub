package com.unihub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.unihub.app.core.common.permission.RequestNotificationPermission
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.core.navigation.AppNavigation
import com.unihub.app.core.navigation.Screen
import com.unihub.app.features.settings.domain.model.ThemeMode
import com.unihub.app.features.settings.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val navigateTo = intent.getStringExtra("NAVIGATE_TO")
        val entityId = intent.getStringExtra("ENTITY_ID")

        setContent {
            val navController = rememberNavController()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.state.collectAsState()
            
            val isDarkTheme = when (settingsState.userPreferences?.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
            }
            
            UniHubTheme(darkTheme = isDarkTheme) {
                RequestNotificationPermission { isGranted ->
                    // Handle permission result if needed
                }
                
                AppNavigation(navController = navController)
            }

            LaunchedEffect(navigateTo, entityId) {
                if (navigateTo != null && entityId != null) {
                    when (navigateTo) {
                        "EVENT" -> navController.navigate(Screen.EventDetail.createRoute(entityId))
                        "TASK", "DEADLINE" -> navController.navigate(Screen.TaskDetail.createRoute(entityId))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    UniHubTheme {
        AppNavigation()
    }
}
