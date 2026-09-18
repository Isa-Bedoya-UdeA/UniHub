package com.unihub.app.core.navigation

sealed class Screen(val route: String) {
    // Root routes
    object Splash : Screen("splash")
    object PermissionGuide : Screen("permission_guide")
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
    object CreateEvent : Screen("event/create?subjectId={subjectId}") {
        fun createRoute(subjectId: String? = null): String {
            return if (subjectId != null) "event/create?subjectId=$subjectId" else "event/create"
        }
    }
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

    object CreateTask : Screen("task/create?subjectId={subjectId}&taskId={taskId}") {
        fun createRoute(subjectId: String? = null, taskId: String? = null): String {
            var r = "task/create"
            val params = mutableListOf<String>()
            if (subjectId != null) params.add("subjectId=$subjectId")
            if (taskId != null) params.add("taskId=$taskId")
            if (params.isNotEmpty()) r += "?" + params.joinToString("&")
            return r
        }
    }
    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: String) = "task/$taskId"
    }

    object AcademicGrades : Screen("academic/grades?subjectId={subjectId}&gradeId={gradeId}") {
        fun createRoute(subjectId: String? = null, gradeId: String? = null): String {
            var r = "academic/grades"
            val params = mutableListOf<String>()
            if (subjectId != null) params.add("subjectId=$subjectId")
            if (gradeId != null) params.add("gradeId=$gradeId")
            if (params.isNotEmpty()) r += "?" + params.joinToString("&")
            return r
        }
    }
    object AcademicPeriods : Screen("academic/periods")
    object GradeDetail : Screen("academic/grade/{gradeId}") {
        fun createRoute(gradeId: String) = "academic/grade/$gradeId"
    }
    object GradeCalculator : Screen("academic/calculator")
    object GradeSimulator : Screen("academic/simulator?subjectId={subjectId}") {
        fun createRoute(subjectId: String) = "academic/simulator?subjectId=$subjectId"
    }

    object AiChat : Screen("ai/chat")

    object SelectLocation : Screen("location/select")

    object Profile : Screen("settings/profile")
    object ManageStudies : Screen("settings/studies")
    object Notifications : Screen("settings/notifications")
    object Security : Screen("settings/security")
}
