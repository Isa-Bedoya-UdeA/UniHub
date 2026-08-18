package com.unihub.app.core.navigation

sealed class Screen(val route: String) {
    // Root routes
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Main : Screen("main")

    // Main destinations (Bottom Nav)
    object Dashboard : Screen("dashboard")
    object Calendar : Screen("calendar")
    object Subjects : Screen("subjects")
    object Tasks : Screen("tasks")
    object Academic : Screen("academic")
    object Settings : Screen("settings")

    // Detail and creation routes
    object CreateEvent : Screen("event/create")
    object EventDetail : Screen("event/{eventId}") {
        fun createRoute(eventId: String) = "event/$eventId"
    }
    object EditEvent : Screen("event/{eventId}/edit") {
        fun createRoute(eventId: String) = "event/$eventId/edit"
    }
    
    object CreateSubject : Screen("subject/create")
    object SubjectDetail : Screen("subject/{subjectId}") {
        fun createRoute(subjectId: String) = "subject/$subjectId"
    }
    object EditSubject : Screen("subject/{subjectId}/edit") {
        fun createRoute(subjectId: String) = "subject/$subjectId/edit"
    }

    object CreateTask : Screen("task/create")
    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: String) = "task/$taskId"
    }

    object AcademicGrades : Screen("academic/grades")
    object GradeDetail : Screen("academic/grade/{gradeId}") {
        fun createRoute(gradeId: String) = "academic/grade/$gradeId"
    }
    object GradeCalculator : Screen("academic/calculator")
    object GradeSimulator : Screen("academic/simulator")

    object AiChat : Screen("ai/chat")

    object Profile : Screen("settings/profile")
    object Notifications : Screen("settings/notifications")
    object Security : Screen("settings/security")
}
