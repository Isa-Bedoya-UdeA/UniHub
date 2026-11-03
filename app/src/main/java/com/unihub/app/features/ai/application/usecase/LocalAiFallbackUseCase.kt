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

        if (isMutationIntent(normalized)) {
            return null
        }

        if (isConversationalOrQuestion(normalized)) {
            return null
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
        if (isConversationalOrQuestion(normalized)) {
            return null
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

    private fun normalizeInput(text: String): String {
        val normalized = Normalizer.normalize(text.lowercase().trim(), Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun isConversationalOrQuestion(input: String): Boolean {
        val conversationalKeywords = listOf(
            "app", "apps", "aplicacion", "aplicaciones", "herramienta", "herramientas",
            "juego", "juegos", "estudiar", "estudio", "repasar", "repaso", "aprender",
            "aprendizaje", "explicar", "explica", "explicame", "recomienda", "recomiendame",
            "recomendacion", "recomendaciones", "sugiere", "sugerencia", "sugerencias",
            "ayuda", "ayudame", "consejo", "consejos", "tip", "tips", "resumen", "resumir",
            "resumeme", "como puedo", "como se", "por que", "porque", "para que", "sirve",
            "sirva", "servir", "pagina", "paginas", "web", "libro", "libros", "video",
            "videos", "tutorial", "tutoriales", "guia", "guias", "ejemplo", "ejemplos",
            "hay alguna", "hay algun", "existe alguna", "existe algun", "conoces",
            "dime sobre", "cuentame", "que opinas", "que piensas", "diferencia entre",
            "concepto", "significa", "teoria", "practicar", "metodo", "tecnica"
        )

        return conversationalKeywords.any { keyword ->
            if (keyword.contains(" ")) {
                input.contains(keyword)
            } else {
                "\\b${Regex.escape(keyword)}\\b".toRegex().containsMatchIn(input)
            }
        }
    }

    private fun isMutationIntent(input: String): Boolean {
        val mutationVerbs = listOf(
            "crear", "creame", "crea", "crear",
            "agregar", "agregame", "agrega", "agregale",
            "registrar", "registra", "registrame",
            "anadir", "anadime", "anade", "añadir", "añademe", "añade",
            "guardar", "guarda", "guardame",
            "programar", "programa", "programame",
            "asignar", "asigna", "asigname",
            "poner", "ponme", "pon",
            "hacer", "hazme", "haz",
            "insertar", "inserta",
            "nueva", "nuevo"
        )

        val patterns = listOf(
            "\\bcrea(r|me|s)?\\b".toRegex(),
            "\\bagrega(r|me|s|le)?\\b".toRegex(),
            "\\bregistra(r|me|s)?\\b".toRegex(),
            "\\ba[nñ]ade(r|me|s)?\\b".toRegex(),
            "\\bguarda(r|me|s)?\\b".toRegex(),
            "\\bprograma(r|me|s)?\\b".toRegex(),
            "\\basigna(r|me|s)?\\b".toRegex(),
            "\\bpon(me|ga|)?\\b".toRegex(),
            "\\bhaz(me)?\\b".toRegex(),
            "\\bnuev[oa]\\b".toRegex()
        )

        return patterns.any { it.containsMatchIn(input) } ||
                mutationVerbs.any { input.contains(it) }
    }

    private fun isGreeting(input: String): Boolean {
        val greetings = setOf(
            "hola", "hi", "hello", "buenos dias", "buenas tardes", "buenas noches", "hey",
            "saludos", "que tal", "buenas"
        )
        return greetings.contains(input) || greetings.any { input.startsWith("$it ") || input.endsWith(" $it") }
    }

    private fun isEventsRequest(input: String): Boolean {
        if (input.contains("tarea") || input.contains("tareas") || input.contains("pendiente") || input.contains("pendientes")) {
            return false
        }

        val exactMatches = setOf("evento", "eventos", "mis eventos", "clase", "clases", "mis clases", "agenda", "mi agenda")
        if (exactMatches.contains(input)) return true

        val eventPatterns = listOf(
            "\\b(que|cuales)\\s+(son\\s+)?(mis\\s+)?(eventos|clases)\\b".toRegex(),
            "\\b(que|cuales)\\s+(eventos|clases)\\s+tengo\\b".toRegex(),
            "\\b(mis\\s+)?(eventos|clases)\\s+(de\\s+hoy|para\\s+hoy|de\\s+ma[nñ]ana|para\\s+ma[nñ]ana)\\b".toRegex(),
            "\\b(ver|mostrar|listar|dime|revisar)\\s+(mis\\s+)?(eventos|clases)\\b".toRegex(),
            "\\bque\\s+tengo\\s+hoy\\b".toRegex(),
            "\\beventos\\s+(de\\s+)?(hoy|ma[nñ]ana)\\b".toRegex(),
            "\\bclases\\s+(de\\s+)?(hoy|ma[nñ]ana)\\b".toRegex(),
            "\\bmi\\s+agenda\\s+(de\\s+)?hoy\\b".toRegex(),
            "\\bmis\\s+eventos\\b".toRegex(),
            "\\bmis\\s+clases\\b".toRegex()
        )

        return eventPatterns.any { it.containsMatchIn(input) }
    }

    private fun isTasksRequest(input: String): Boolean {
        if (input.contains("evento") || input.contains("eventos")) return false

        val exactMatches = setOf("tarea", "tareas", "mis tareas", "pendiente", "pendientes", "mis pendientes", "tareas pendientes")
        if (exactMatches.contains(input)) return true

        val taskPatterns = listOf(
            "\\b(que|cuales)\\s+(son\\s+)?(mis\\s+)?(tareas|pendientes)\\b".toRegex(),
            "\\b(que|cuales)\\s+(tareas|pendientes)\\s+tengo\\b".toRegex(),
            "\\b(mis\\s+)?(tareas|pendientes)\\s+(de\\s+hoy|para\\s+hoy|de\\s+ma[nñ]ana|para\\s+ma[nñ]ana)\\b".toRegex(),
            "\\b(ver|mostrar|listar|dime|revisar)\\s+(mis\\s+)?(tareas|pendientes)\\b".toRegex(),
            "\\btengo\\s+(alguna\\s+)?(tareas|pendientes|tarea|pendiente)\\b".toRegex(),
            "\\btareas\\s+pendientes\\b".toRegex(),
            "\\bmis\\s+tareas\\b".toRegex(),
            "\\bmis\\s+pendientes\\b".toRegex()
        )

        return taskPatterns.any { it.containsMatchIn(input) }
    }

    private fun isDailyPlanRequest(input: String): Boolean {
        val dailyPlanPatterns = listOf(
            "\\bplan\\s+(del\\s+)?dia\\b".toRegex(),
            "\\borganiza(r|me)?\\s+(mi\\s+)?dia\\b".toRegex(),
            "\\bque\\s+tengo\\s+que\\s+hacer\\s+hoy\\b".toRegex(),
            "\\bcomo\\s+esta\\s+mi\\s+dia\\b".toRegex(),
            "\\b(mi\\s+)?agenda\\s+(de\\s+)?hoy\\b".toRegex(),
            "\\borganiza(r|me)?\\s+mis\\s+actividades\\b".toRegex(),
            "\\bplan\\s+de\\s+hoy\\b".toRegex(),
            "\\bitinerario\\s+(de\\s+)?hoy\\b".toRegex()
        )
        return dailyPlanPatterns.any { it.containsMatchIn(input) }
    }

    private fun isEventsAndTasksRequest(input: String): Boolean {
        val exactMatches = setOf("agenda academica", "mi agenda academica")
        if (exactMatches.contains(input)) return true

        val combinedPatterns = listOf(
            "\\b(tareas\\s+y\\s+eventos|eventos\\s+y\\s+tareas)\\b".toRegex(),
            "\\b(clases\\s+y\\s+tareas|tareas\\s+y\\s+clases)\\b".toRegex(),
            "\\bque\\s+tengo\\s+pendiente\\s+y\\s+que\\s+eventos\\s+tengo\\b".toRegex(),
            "\\bmi\\s+agenda\\s+academica\\b".toRegex()
        )
        return combinedPatterns.any { it.containsMatchIn(input) }
    }

    private fun isSubjectsRequest(input: String): Boolean {
        val exactMatches = setOf("materias", "mis materias", "asignaturas", "mis asignaturas", "cursos", "mis cursos")
        if (exactMatches.contains(input)) return true

        val subjectPatterns = listOf(
            "\\b(que|cuales)\\s+(son\\s+)?(mis\\s+)?(materias|asignaturas|cursos)\\b".toRegex(),
            "\\b(que|cuales)\\s+(materias|asignaturas|cursos)\\s+tengo\\b".toRegex(),
            "\\b(materias|asignaturas|cursos)\\s+actuales\\b".toRegex(),
            "\\b(ver|mostrar|listar|dime|revisar)\\s+(mis\\s+)?(materias|asignaturas|cursos)\\b".toRegex(),
            "\\bmis\\s+materias\\b".toRegex(),
            "\\bmis\\s+asignaturas\\b".toRegex(),
            "\\bmis\\s+cursos\\b".toRegex()
        )

        return subjectPatterns.any { it.containsMatchIn(input) }
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
