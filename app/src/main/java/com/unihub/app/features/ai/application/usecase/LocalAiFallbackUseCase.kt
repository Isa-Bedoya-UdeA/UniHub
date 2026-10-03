package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.events.application.usecase.GetEventsUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.tasks.application.usecase.GetTasksUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus
import kotlinx.coroutines.flow.first
import java.text.Normalizer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LocalAiFallbackUseCase @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase
) {
    suspend fun handleDeterministicIntent(input: String, userId: String): String? {
        val normalized = normalizeInput(input)

        if (isGreeting(normalized)) {
            return "Hola, ¿en qué puedo ayudarte?"
        }

        if (isDailyPlanRequest(normalized)) {
            return buildDailyPlan(userId)
        }

        if (isEventsAndTasksRequest(normalized)) {
            return buildEventsAndTasksResponse(userId)
        }

        if (isEventsRequest(normalized)) {
            return buildEventsResponse(userId)
        }

        if (isTasksRequest(normalized)) {
            return buildTasksResponse(userId)
        }

        if (isSubjectsRequest(normalized)) {
            return buildSubjectsResponse(userId)
        }

        return null
    }

    suspend fun handleFallbackForFailedAi(input: String, userId: String): String? {
        val normalized = normalizeInput(input)
        if (normalized.contains("evento") || normalized.contains("clase")) {
            return buildEventsResponse(userId)
        }
        if (normalized.contains("tarea") || normalized.contains("pendiente")) {
            return buildTasksResponse(userId)
        }
        if (normalized.contains("materia") || normalized.contains("asignatura")) {
            return buildSubjectsResponse(userId)
        }
        if (normalized.contains("plan") || normalized.contains("agenda") || normalized.contains("horario")) {
            return buildDailyPlan(userId)
        }
        return null
    }

    private fun normalizeInput(text: String): String {
        val normalized = Normalizer.normalize(text.lowercase().trim(), Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun isGreeting(input: String): Boolean {
        val greetings = setOf(
            "hola", "hi", "hello", "buenos dias", "buenas tardes", "buenas noches", "hey",
            "saludos", "que tal", "buenas"
        )
        return greetings.contains(input) || greetings.any { input.startsWith("$it ") || input.endsWith(" $it") }
    }

    private fun isEventsRequest(input: String): Boolean {
        return input.contains("evento") || input.contains("eventos") ||
                input.contains("que tengo hoy") || input.contains("eventos hoy") ||
                input.contains("eventos manana") || input.contains("que eventos tengo") ||
                input.contains("mi agenda de hoy") || input.contains("mis eventos")
    }

    private fun isTasksRequest(input: String): Boolean {
        return (input.contains("tarea") || input.contains("tareas") || input.contains("pendiente") || input.contains("pendientes")) &&
                !input.contains("evento") && !input.contains("eventos")
    }

    private fun isDailyPlanRequest(input: String): Boolean {
        return input.contains("plan del dia") || input.contains("organiza mi dia") ||
                input.contains("que tengo que hacer hoy") || input.contains("como esta mi dia") ||
                input.contains("mi agenda") || input.contains("organiza mis actividades") ||
                input.contains("plan de hoy")
    }

    private fun isEventsAndTasksRequest(input: String): Boolean {
        return (input.contains("tarea") || input.contains("tareas")) && (input.contains("evento") || input.contains("eventos")) ||
                input.contains("que tengo pendiente y que eventos tengo") || input.contains("mi agenda academica")
    }

    private fun isSubjectsRequest(input: String): Boolean {
        return input.contains("mis materias") || input.contains("materias actuales") ||
                input.contains("que materias tengo") || input.contains("materias") ||
                input.contains("mis asignaturas") || input.contains("que asignaturas tengo")
    }

    private suspend fun buildEventsResponse(userId: String): String {
        val today = LocalDate.now(ZoneId.systemDefault())
        val tomorrow = today.plusDays(1)
        val todayStr = today.toString()
        val tomorrowStr = tomorrow.toString()

        val allEvents = try {
            getEventsUseCase(userId, todayStr, tomorrowStr).first()
        } catch (_: Exception) {
            emptyList()
        }

        val relevantEvents = allEvents.filter { eventOccursOnDates(it, today, tomorrow) }

        if (relevantEvents.isEmpty()) {
            return "No tienes eventos programados para hoy o mañana."
        }

        val subjectMap = getSubjectMap(userId)
        val sb = StringBuilder("📌 **Eventos para hoy y mañana:**\n\n")

        relevantEvents.sortedBy { it.startAt }.forEach { event ->
            val subjectName = event.subjectId?.let { subjectMap[it] }
            val timeDisplay = formatTimeRange(event.startAt, event.endAt)
            val dateLabel = getDateLabel(event.startAt, today, tomorrow)
            val locationDisplay = when (event.locationType) {
                LocationType.REMOTE -> event.meetingUrl?.let { "Virtual ($it)" } ?: "Virtual"
                LocationType.PHYSICAL -> if (!event.notes.isNullOrBlank()) "Presencial: ${event.notes}" else "Presencial"
                LocationType.NONE -> "Sin ubicación"
            }

            sb.append("• **${event.title}**")
            if (!subjectName.isNullOrBlank()) {
                sb.append(" ($subjectName)")
            }
            sb.append("\n  📅 $dateLabel, $timeDisplay | $locationDisplay\n\n")
        }

        return sb.toString().trimEnd()
    }

    private suspend fun buildTasksResponse(userId: String): String {
        val today = LocalDate.now(ZoneId.systemDefault())
        val tomorrow = today.plusDays(1)

        val tasks = try {
            getTasksUseCase(userId).first()
                .filter { it.status != TaskStatus.COMPLETED }
                .filter { taskRelevantForDates(it, today, tomorrow) }
        } catch (_: Exception) {
            emptyList()
        }

        if (tasks.isEmpty()) {
            return "No tienes tareas pendientes para hoy o mañana."
        }

        val subjectMap = getSubjectMap(userId)
        val sb = StringBuilder("📝 **Tareas pendientes:**\n\n")

        tasks.sortedBy { it.dueAt ?: "9999" }.forEach { task ->
            val subjectName = task.subjectId?.let { subjectMap[it] }
            val dateLabel = task.dueAt?.let { getDateLabel(it, today, tomorrow) } ?: "Sin fecha"
            val timeLabel = task.dueAt?.let { formatSingleTime(it) } ?: ""
            val priorityLabel = when (task.priority) {
                TaskPriority.HIGH -> "Alta"
                TaskPriority.MEDIUM -> "Media"
                TaskPriority.LOW -> "Baja"
            }
            val statusLabel = when (task.status) {
                TaskStatus.PENDING -> "Pendiente"
                TaskStatus.IN_PROGRESS -> "En progreso"
                TaskStatus.COMPLETED -> "Completada"
            }

            sb.append("• **${task.title}**")
            if (!subjectName.isNullOrBlank()) {
                sb.append(" ($subjectName)")
            }
            sb.append("\n  📅 Vence: $dateLabel $timeLabel | Estado: $statusLabel | Prioridad: $priorityLabel\n\n")
        }

        return sb.toString().trimEnd()
    }

    private suspend fun buildDailyPlan(userId: String): String {
        val today = LocalDate.now(ZoneId.systemDefault())
        val tomorrow = today.plusDays(1)
        val todayStr = today.toString()
        val tomorrowStr = tomorrow.toString()

        val events = try {
            getEventsUseCase(userId, todayStr, tomorrowStr).first()
                .filter { eventOccursOnDates(it, today, tomorrow) }
        } catch (_: Exception) {
            emptyList()
        }

        val tasks = try {
            getTasksUseCase(userId).first()
                .filter { it.status != TaskStatus.COMPLETED }
                .filter { taskRelevantForDates(it, today, tomorrow) }
        } catch (_: Exception) {
            emptyList()
        }

        if (events.isEmpty() && tasks.isEmpty()) {
            return "No tienes eventos ni tareas pendientes para hoy o mañana."
        }

        val subjectMap = getSubjectMap(userId)

        data class PlanItem(
            val title: String,
            val time: String,
            val sortKey: String,
            val isTimed: Boolean
        )

        val items = mutableListOf<PlanItem>()

        events.forEach { e ->
            val subjectName = e.subjectId?.let { subjectMap[it] } ?: ""
            val subjectSuffix = if (subjectName.isNotBlank()) " ($subjectName)" else ""
            val timeFormatted = formatSingleTime(e.startAt)
            items.add(
                PlanItem(
                    title = "${e.title}$subjectSuffix",
                    time = timeFormatted,
                    sortKey = e.startAt,
                    isTimed = true
                )
            )
        }

        tasks.forEach { t ->
            val subjectName = t.subjectId?.let { subjectMap[it] } ?: ""
            val subjectSuffix = if (subjectName.isNotBlank()) " ($subjectName)" else ""
            val hasTime = t.dueAt != null && (t.dueAt.contains("T") || t.dueAt.contains(" "))
            val timeFormatted = if (t.dueAt != null) formatSingleTime(t.dueAt) else "Sin hora"
            items.add(
                PlanItem(
                    title = "Entrega: ${t.title}$subjectSuffix",
                    time = timeFormatted,
                    sortKey = t.dueAt ?: "9999",
                    isTimed = hasTime
                )
            )
        }

        items.sortBy { it.sortKey }

        val sb = StringBuilder("Este es tu plan para hoy:\n\n")
        items.forEach { item ->
            sb.append("${item.time} — ${item.title}\n")
        }

        return sb.toString().trimEnd()
    }

    private suspend fun buildEventsAndTasksResponse(userId: String): String {
        val eventsPart = buildEventsResponse(userId)
        val tasksPart = buildTasksResponse(userId)
        return "$eventsPart\n\n$tasksPart"
    }

    private suspend fun buildSubjectsResponse(userId: String): String {
        val subjects = try {
            getAllSubjectsUseCase(userId).first()
        } catch (_: Exception) {
            emptyList()
        }

        if (subjects.isEmpty()) {
            return "No tienes materias registradas."
        }

        val sb = StringBuilder("📚 **Tus materias actuales:**\n\n")
        subjects.forEach { subject ->
            sb.append("• **${subject.name}**")
            if (!subject.code.isNullOrBlank()) {
                sb.append(" (${subject.code})")
            }
            val professor = subject.professor?.let { " | Prof. $it" } ?: ""
            val credits = subject.credits?.let { " | $it créditos" } ?: ""
            sb.append("$professor$credits\n")
        }

        return sb.toString().trimEnd()
    }

    private fun eventOccursOnDates(event: Event, today: LocalDate, tomorrow: LocalDate): Boolean {
        if (event.recurrenceDays.isNotEmpty()) {
            if (today.dayOfWeek.value in event.recurrenceDays || tomorrow.dayOfWeek.value in event.recurrenceDays) {
                return true
            }
        }
        val eventDate = parseLocalDate(event.startAt)
        return eventDate == today || eventDate == tomorrow
    }

    private fun taskRelevantForDates(task: Task, today: LocalDate, tomorrow: LocalDate): Boolean {
        if (task.dueAt == null) return true
        val taskDate = parseLocalDate(task.dueAt) ?: return true
        return taskDate <= tomorrow
    }

    private fun parseLocalDate(dateStr: String): LocalDate? {
        return try {
            if (dateStr.contains("T")) {
                LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
            } else if (dateStr.contains(" ")) {
                LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toLocalDate()
            } else if (dateStr.length >= 10) {
                LocalDate.parse(dateStr.take(10))
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getSubjectMap(userId: String): Map<String, String> {
        return try {
            getAllSubjectsUseCase(userId).first().associate { it.id to it.name }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun getDateLabel(dateStr: String, today: LocalDate, tomorrow: LocalDate): String {
        val date = parseLocalDate(dateStr) ?: return "Hoy"
        return when (date) {
            today -> "Hoy"
            tomorrow -> "Mañana"
            else -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }

    private fun formatTimeRange(startStr: String, endStr: String): String {
        val start = formatSingleTime(startStr)
        val end = formatSingleTime(endStr)
        return if (start.isNotBlank() && end.isNotBlank() && start != end) "$start - $end" else start
    }

    private fun formatSingleTime(dateStr: String): String {
        return try {
            if (dateStr.contains("T")) {
                LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern("HH:mm"))
            } else if (dateStr.contains(" ")) {
                LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).format(DateTimeFormatter.ofPattern("HH:mm"))
            } else if (dateStr.length >= 16 && dateStr[10] == 'T') {
                dateStr.substring(11, 16)
            } else {
                "08:00"
            }
        } catch (_: Exception) {
            "08:00"
        }
    }
}
