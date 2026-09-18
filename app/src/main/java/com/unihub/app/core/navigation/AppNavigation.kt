package com.unihub.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.unihub.app.core.designsystem.component.foundation.UniHubBottomNavigation
import com.unihub.app.core.designsystem.component.foundation.UniHubTopBar
import com.unihub.app.core.designsystem.theme.UniHubTheme
import com.unihub.app.core.util.PreferencesManager
import com.unihub.app.features.academic.presentation.screen.AcademicPeriodsScreen
import com.unihub.app.features.academic.presentation.screen.AcademicScreen
import com.unihub.app.features.academic.presentation.screen.CreateGradeScreen
import com.unihub.app.features.academic.presentation.screen.GradeSimulatorScreen
import com.unihub.app.features.auth.presentation.screen.LoginScreen
import com.unihub.app.features.auth.presentation.screen.OnboardingScreen
import com.unihub.app.features.auth.presentation.screen.SplashScreen
import com.unihub.app.features.calendar.presentation.screen.CalendarScreen
import com.unihub.app.features.dashboard.presentation.screen.DashboardScreen
import com.unihub.app.features.events.presentation.screen.CreateEventScreen
import com.unihub.app.features.events.presentation.screen.EventDetailsScreen
import com.unihub.app.features.location.presentation.screen.LocationSelectionScreen
import com.unihub.app.features.settings.presentation.screen.EditProfileScreen
import com.unihub.app.features.settings.presentation.screen.ManageStudiesScreen
import com.unihub.app.features.settings.presentation.screen.PermissionGuideScreen
import com.unihub.app.features.settings.presentation.screen.SettingsScreen
import com.unihub.app.features.subjects.presentation.screen.CreateSubjectScreen
import com.unihub.app.features.subjects.presentation.screen.EditSubjectScreen
import com.unihub.app.features.subjects.presentation.screen.SubjectDetailsScreen
import com.unihub.app.features.subjects.presentation.screen.SubjectsScreen
import com.unihub.app.features.tasks.presentation.screen.CreateTaskScreen
import com.unihub.app.features.tasks.presentation.screen.TasksScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if current route is a bottom nav destination
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Calendar.route,
        Screen.Subjects.route,
        Screen.Tasks.route,
        Screen.Academic.route,
        Screen.Settings.route
    )
    
    val showTopBar = currentRoute !in listOf(
        Screen.Splash.route,
        Screen.PermissionGuide.route,
        Screen.Onboarding.route,
        Screen.Login.route
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                UniHubTopBar(
                    onBackClick = if (!showBottomBar) { { navController.popBackStack() } } else null
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                UniHubBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
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
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Flow
            composable(Screen.Splash.route) {
                SplashScreen { isAuthenticated ->
                    if (isAuthenticated) {
                        // User is already authenticated, go directly to Dashboard
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        // User is not authenticated, check onboarding flow
                        val permissionGuideShown = PreferencesManager.isPermissionGuideShown(context)
                        val onboardingShown = PreferencesManager.isOnboardingShown(context)
                        val hasNotificationPermission = checkNotificationPermission(context)
                        
                        when {
                            !hasNotificationPermission -> {
                                navController.navigate(Screen.PermissionGuide.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                            !onboardingShown -> {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                            else -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
            composable(Screen.PermissionGuide.route) {
                PermissionGuideScreen {
                    PreferencesManager.setPermissionGuideShown(context, true)
                    val onboardingShown = PreferencesManager.isOnboardingShown(context)
                    if (!onboardingShown) {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.PermissionGuide.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.PermissionGuide.route) { inclusive = true }
                        }
                    }
                }
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen {
                    PreferencesManager.setOnboardingShown(context, true)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }
            composable(Screen.Login.route) {
                LoginScreen {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            // Main destinations
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToSubjects = { navController.navigate(Screen.Subjects.route) },
                    onNavigateToEvent = { id ->
                        navController.navigate(Screen.EventDetail.createRoute(id))
                    },
                    onNavigateToSubjectDetail = { id ->
                        navController.navigate(Screen.SubjectDetail.createRoute(id))
                    }
                )
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onNavigateToEvent = { id ->
                        navController.navigate(Screen.EventDetail.createRoute(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.CreateEvent.route)
                    }
                )
            }
            composable(Screen.Subjects.route) {
                SubjectsScreen(
                    onNavigateToDetails = { id -> 
                        navController.navigate(Screen.SubjectDetail.createRoute(id)) 
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.CreateSubject.route)
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.EditSubject.createRoute(id))
                    }
                )
            }
            composable(
                route = Screen.SubjectDetail.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                SubjectDetailsScreen(
                    subjectId = subjectId,
                    onNavigateToEvent = { id -> 
                        navController.navigate(Screen.EventDetail.createRoute(id)) 
                    },
                    onBack = { navController.popBackStack() },
                    onNavigateToCreateTask = {
                        navController.navigate(Screen.CreateTask.createRoute(subjectId = subjectId))
                    },
                    onNavigateToCreateGrade = {
                        navController.navigate(Screen.AcademicGrades.createRoute(subjectId = subjectId))
                    },
                    onNavigateToEditGrade = { gradeId ->
                        navController.navigate(Screen.AcademicGrades.createRoute(subjectId = subjectId, gradeId = gradeId))
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(Screen.CreateTask.createRoute(subjectId = subjectId, taskId = taskId))
                    },
                    onNavigateToSimulator = {
                        navController.navigate(Screen.GradeSimulator.createRoute(subjectId))
                    },
                    onNavigateToCreateEvent = {
                        navController.navigate(Screen.CreateEvent.createRoute(subjectId = subjectId))
                    }
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onNavigateToCreate = {
                        navController.navigate(Screen.CreateTask.route)
                    }
                )
            }
            composable(Screen.Academic.route) {
                AcademicScreen(
                    onNavigateToSubjectDetail = { id ->
                        navController.navigate(Screen.SubjectDetail.createRoute(id))
                    },
                    onNavigateToManagePeriods = {
                        navController.navigate(Screen.AcademicPeriods.route)
                    }
                )
            }
            composable(Screen.AcademicPeriods.route) {
                AcademicPeriodsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.GradeSimulator.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                GradeSimulatorScreen(
                    subjectId = subjectId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                val authViewModel: com.unihub.app.features.auth.presentation.viewmodel.AuthViewModel = hiltViewModel()
                SettingsScreen(
                    onLogout = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToManageStudies = {
                        navController.navigate(Screen.ManageStudies.route)
                    },
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
            composable(Screen.ManageStudies.route) {
                ManageStudiesScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                EditProfileScreen(
                    onProfileUpdated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SelectLocation.route) {
                LocationSelectionScreen(
                    onLocationConfirmed = { locationData ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("location_result", locationData)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Creation Screens
            composable(Screen.CreateSubject.route) {
                CreateSubjectScreen(
                    onSubjectCreated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.CreateTask.route,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("taskId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId")
                val taskId = backStackEntry.arguments?.getString("taskId")
                CreateTaskScreen(
                    subjectId = subjectId,
                    taskId = taskId,
                    onTaskCreated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.CreateEvent.route,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId")
                val locationResult = backStackEntry.savedStateHandle.get<String>("location_result")
                
                CreateEventScreen(
                    subjectId = subjectId,
                    locationResult = locationResult,
                    onEventCreated = { navController.popBackStack() },
                    onSelectLocation = { navController.navigate(Screen.SelectLocation.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.EditEvent.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val locationResult = backStackEntry.savedStateHandle.get<String>("location_result")
                
                CreateEventScreen(
                    eventId = eventId,
                    locationResult = locationResult,
                    onEventCreated = { navController.popBackStack() },
                    onSelectLocation = { navController.navigate(Screen.SelectLocation.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.AcademicGrades.route,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("gradeId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                val gradeId = backStackEntry.arguments?.getString("gradeId")
                CreateGradeScreen(
                    subjectId = subjectId,
                    gradeId = gradeId,
                    onGradeCreated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            // Edit Screens
            composable(
                route = Screen.EditSubject.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                EditSubjectScreen(
                    subjectId = subjectId,
                    onSubjectUpdated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Event Details
            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventDetailsScreen(
                    eventId = eventId,
                    onBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> 
                        navController.navigate(Screen.EditEvent.createRoute(id))
                    }
                )
            }
        }
    }
}

private fun checkNotificationPermission(context: android.content.Context): Boolean {
    return androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
}
