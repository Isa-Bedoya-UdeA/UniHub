package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.domain.model.TaskStatus
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class BuildAiContextUseCase @Inject constructor(
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val getStudiesUseCase: GetStudiesUseCase,
    private val getAcademicPeriodsUseCase: GetAcademicPeriodsUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val getGradesBySubjectUseCase: GetGradesBySubjectUseCase,
    private val getAcademicSummaryUseCase: GetAcademicSummaryUseCase
) {
    suspend operator fun invoke(): String {
        val userId = getCurrentUidUseCase() ?: return "Fecha actual: ${getFormattedCurrentDateTime()}"

        val now = LocalDateTime.now(ZoneId.systemDefault())

        val sb = StringBuilder()
        sb.append("FECHA Y HORA ACTUAL: ${getFormattedCurrentDateTime()}\n\n")

        val studies = try { getStudiesUseCase(userId).first() } catch (_: Exception) { emptyList() }
        val periods = try { getAcademicPeriodsUseCase(userId).first() } catch (_: Exception) { emptyList() }
        val subjects = try { getAllSubjectsUseCase(userId).first() } catch (_: Exception) { emptyList() }
        val activeStudy = studies.find { it.isActive } ?: studies.singleOrNull()

        if (studies.isNotEmpty()) {
            sb.append("PROGRAMAS ACADÉMICOS (ESTUDIOS):\n")
            studies.forEach { study ->
                val activeFlag = if (study.isActive) " [ACTIVO]" else ""
                sb.append("- ID: ${study.id} | Nombre: ${study.name} | Institución: ${study.institution}$activeFlag\n")
            }
            sb.append("\n")
        }

        if (periods.isNotEmpty()) {
            sb.append("PERIODOS ACADÉMICOS:\n")
            periods.forEach { period ->
                val currentFlag = if (period.isCurrent) " [PERIODO ACTUAL]" else ""
                sb.append("- ID: ${period.id} | Nombre: ${period.name} | ProgramaID: ${period.studyId}$currentFlag\n")
            }
            sb.append("\n")
        }

        if (activeStudy != null) {
            try {
                val summary = getAcademicSummaryUseCase(userId, activeStudy.id).first()
                sb.append("RESUMEN ACADÉMICO (${activeStudy.name}):\n")
                sb.append("- Promedio acumulado: ${summary.cumulativeGpa}\n")
                sb.append("- Promedio semestre actual: ${summary.currentSemesterGpa}\n")
                sb.append("- Créditos aprobados: ${summary.earnedCredits}/${summary.targetCredits}\n")
                sb.append("- Porcentaje completado: ${summary.progressPercentage}%\n\n")
            } catch (_: Exception) {}
        }

        val subjectMap = mutableMapOf<String, String>()
        if (subjects.isNotEmpty()) {
            sb.append("MATERIAS REGISTRADAS:\n")
            subjects.forEach { subject ->
                subjectMap[subject.id] = subject.name
                val codeStr = if (!subject.code.isNullOrBlank()) " (${subject.code})" else ""
                val profStr = if (!subject.professor.isNullOrBlank()) " | Prof: ${subject.professor}" else ""
                val creditsStr = if (subject.credits != null) " | ${subject.credits} cr" else ""
                sb.append("- ID: ${subject.id} | Nombre: ${subject.name}$codeStr$profStr$creditsStr | PeriodoID: ${subject.academicPeriodId}\n")
            }
            sb.append("\n")
        } else {
            sb.append("MATERIAS REGISTRADAS: Ninguna\n\n")
        }

        val currentPeriod = periods.find { it.isCurrent }
        val currentPeriodSubjects = if (currentPeriod != null) {
            subjects.filter { it.academicPeriodId == currentPeriod.id }
        } else subjects

        if (currentPeriodSubjects.isNotEmpty()) {
            sb.append("NOTAS REGISTRADAS:\n")
            var hasGrades = false
            currentPeriodSubjects.forEach { subject ->
                try {
                    val grades = getGradesBySubjectUseCase(subject.id).first()
                    if (grades.isNotEmpty()) {
                        hasGrades = true
                        grades.forEach { grade ->
                            sb.append("- Materia: ${subject.name} | Nota: ${grade.name} | Valor: ${grade.value} | Peso: ${grade.weight}%\n")
                        }
                    }
                } catch (_: Exception) {}
            }
            if (!hasGrades) {
                sb.append("No hay notas registradas para las materias del periodo actual.\n")
            }
            sb.append("\n")
        }

        try {
            val tasks = getTasksUseCase(userId).first()
                .filter { it.status != TaskStatus.COMPLETED }
            if (tasks.isNotEmpty()) {
                sb.append("TAREAS PENDIENTES:\n")
                tasks.take(10).forEach { task ->
                    val subjName = task.subjectId?.let { subjectMap[it] } ?: "Sin materia"
                    val dueStr = task.dueAt ?: "Sin fecha"
                    sb.append("- Tarea: ${task.title} | Materia: $subjName | Vence: $dueStr | Prioridad: ${task.priority}\n")
                }
                sb.append("\n")
            } else {
                sb.append("TAREAS PENDIENTES: Ninguna\n\n")
            }
        } catch (_: Exception) {}

        try {
            val todayStr = now.toLocalDate().toString()
            val futureStr = now.toLocalDate().plusDays(7).toString()
            val events = getEventsUseCase(userId, todayStr, futureStr).first()
            if (events.isNotEmpty()) {
                sb.append("EVENTOS / PRÓXIMAS CLASES:\n")
                events.take(10).forEach { event ->
                    val subjName = event.subjectId?.let { subjectMap[it] } ?: "General"
                    sb.append("- Evento: ${event.title} | Materia: $subjName | Inicio: ${event.startAt} | Fin: ${event.endAt} | Tipo: ${event.locationType}\n")
                }
                sb.append("\n")
            } else {
                sb.append("EVENTOS PRÓXIMOS: Ninguno\n\n")
            }
        } catch (_: Exception) {}

        return sb.toString().trim()
    }

    private fun getFormattedCurrentDateTime(): String {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val spanishLocale = Locale("es", "ES")
        val dayOfWeek = now.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, spanishLocale)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", spanishLocale)
        return "$dayOfWeek ${now.format(dateFormatter)}"
    }
}
