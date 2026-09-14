package com.unihub.app.features.dashboard.application.usecase

import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class DashboardData(
    val summary: AcademicSummary,
    val upcomingEvents: List<Event>,
    val pendingTasks: List<Task>
)

class GetDashboardDataUseCase @Inject constructor(
    private val getAcademicSummaryUseCase: GetAcademicSummaryUseCase,
    private val getStudiesUseCase: GetStudiesUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val getTasksUseCase: GetTasksUseCase
) {
    operator fun invoke(userId: String): Flow<DashboardData> {
        val today = LocalDate.now(ZoneId.systemDefault()).toString()
        val nextWeek = LocalDate.now(ZoneId.systemDefault()).plusDays(7).toString()

        return getStudiesUseCase(userId).flatMapLatest { studies ->
            val activeStudy = studies.find { it.isActive } ?: studies.firstOrNull()
            val studyId = activeStudy?.id ?: return@flatMapLatest flowOf(
                DashboardData(
                    summary = AcademicSummary(0.0, 0.0, 0, 0, 0.0),
                    upcomingEvents = emptyList(),
                    pendingTasks = emptyList()
                )
            )

            combine(
                getAcademicSummaryUseCase(userId, studyId),
                getEventsUseCase(userId, today, nextWeek),
                getTasksUseCase(userId)
            ) { summary, events, tasks ->
                DashboardData(
                    summary = summary,
                    upcomingEvents = events,
                    pendingTasks = tasks.filter { it.status != com.unihub.app.features.tasks.domain.model.TaskStatus.COMPLETED }
                )
            }
        }
    }
}
