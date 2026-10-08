package com.unihub.app.features.ai.application.usecase

import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsUseCase
import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.application.usecase.SaveGradeUseCase
import com.unihub.app.features.academic.application.usecase.SaveSubjectUseCase
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.events.application.usecase.SaveEventUseCase
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.tasks.application.usecase.SaveTaskUseCase
import com.unihub.app.features.tasks.domain.model.Task
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskReminderType
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.ai.domain.model.AiActionItem
import com.unihub.app.features.ai.domain.model.AiActionType
import com.unihub.app.features.ai.domain.model.AiPendingAction
import com.unihub.app.features.ai.domain.model.AiStructuredResponse
import kotlinx.coroutines.flow.first
import java.text.Normalizer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class ResolveAndExecuteAiActionUseCase @Inject constructor(
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val getStudiesUseCase: GetStudiesUseCase,
    private val getAcademicPeriodsUseCase: GetAcademicPeriodsUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val saveSubjectUseCase: SaveSubjectUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val saveEventUseCase: SaveEventUseCase,
    private val saveGradeUseCase: SaveGradeUseCase
) {
    suspend fun resolve(structured: AiStructuredResponse): AiStructuredResponse {
        val userId = getCurrentUidUseCase() ?: return structured.copy(
            type = AiActionType.CHAT,
            message = "Debes estar autenticado para realizar esta acción.",
            requiresConfirmation = false
        )

        if (structured.type == AiActionType.CHAT ||
            structured.type == AiActionType.ACADEMIC_ASSISTANT ||
            structured.type == AiActionType.PLANNING_RECOMMENDATION ||
            structured.type == AiActionType.CLARIFICATION ||
            structured.items.isEmpty()
        ) {
            return structured
        }

        val studies = try { getStudiesUseCase(userId).first() } catch (_: Exception) { emptyList() }
        val periods = try { getAcademicPeriodsUseCase(userId).first() } catch (_: Exception) { emptyList() }
        val subjects = try { getAllSubjectsUseCase(userId).first() } catch (_: Exception) { emptyList() }

        val activeStudy = studies.find { it.isActive } ?: studies.singleOrNull()
        val currentPeriod = periods.find { it.isCurrent } ?: periods.singleOrNull()

        val resolvedItems = mutableListOf<AiActionItem>()
        val missingFields = mutableListOf<String>()
        val clarificationMessages = mutableListOf<String>()

        for (item in structured.items) {
            val studyId = item.studyId ?: activeStudy?.id
            val periodId = item.academicPeriodId ?: currentPeriod?.id

            var resolvedSubjectId = item.subjectId
            val itemSubjName = item.subjectName ?: item.name

            if (resolvedSubjectId == null && !itemSubjName.isNullOrBlank()) {
                val matchedSubjects = subjects.filter {
                    normalizeText(it.name) == normalizeText(itemSubjName)
                }
                when {
                    matchedSubjects.size == 1 -> {
                        resolvedSubjectId = matchedSubjects.first().id
                    }
                    matchedSubjects.size > 1 -> {
                        val names = matchedSubjects.map { it.name }.distinct().joinToString(", ")
                        clarificationMessages.add(
                            "Hay varias materias llamadas \"$itemSubjName\": $names. ¿A cuál te refieres?"
                        )
                    }
                }
            }

            val resolvedItem = when (structured.type) {
                AiActionType.CREATE_SUBJECT -> {
                    val name = item.name ?: item.title
                    if (name.isNullOrBlank()) {
                        missingFields.add("nombre_materia")
                    }
                    if (studyId == null) {
                        missingFields.add("programa_academico")
                    }
                    if (periodId == null) {
                        missingFields.add("periodo_academico")
                    }
                    item.copy(
                        name = name,
                        studyId = studyId,
                        academicPeriodId = periodId
                    )
                }
                AiActionType.CREATE_TASK -> {
                    val title = item.title ?: item.name
                    if (title.isNullOrBlank()) {
                        missingFields.add("titulo_tarea")
                    }
                    if (resolvedSubjectId == null && !itemSubjName.isNullOrBlank()) {
                        val matchedSubjects = subjects.filter {
                            normalizeText(it.name) == normalizeText(itemSubjName)
                        }
                        if (matchedSubjects.isEmpty()) {
                            missingFields.add("materia_no_encontrada")
                        }
                    }
                    val formattedDueAt = resolveDateTime(item.dueAt, isEnd = false, defaultHour = 23, defaultMinute = 59)
                    item.copy(
                        title = title,
                        subjectId = resolvedSubjectId,
                        academicPeriodId = periodId,
                        dueAt = formattedDueAt
                    )
                }
                AiActionType.CREATE_EVENT -> {
                    val title = item.title ?: item.name
                    if (title.isNullOrBlank()) {
                        missingFields.add("titulo_evento")
                    }
                    val startAt = resolveDateTime(item.startAt, isEnd = false, defaultHour = 8, defaultMinute = 0)
                    var endAt = resolveDateTime(item.endAt, isEnd = true, defaultHour = 9, defaultMinute = 0)

                    if (endAt <= startAt) {
                        endAt = addOneHour(startAt)
                    }

                    item.copy(
                        title = title,
                        subjectId = resolvedSubjectId,
                        academicPeriodId = periodId,
                        startAt = startAt,
                        endAt = endAt
                    )
                }
                AiActionType.REGISTER_GRADE -> {
                    val value = item.value
                    if (value == null || value < 0.0 || value > 5.0) {
                        missingFields.add("valor_nota_valido_0_a_5")
                    }
                    if (resolvedSubjectId == null) {
                        val matchedSubjects = if (!itemSubjName.isNullOrBlank()) {
                            subjects.filter { normalizeText(it.name) == normalizeText(itemSubjName) }
                        } else emptyList()
                        if (matchedSubjects.isEmpty()) {
                            missingFields.add("materia")
                        }
                    }
                    val name = item.name ?: item.title ?: "Nota"
                    val weight = item.weight ?: 0.0
                    item.copy(
                        name = name,
                        subjectId = resolvedSubjectId,
                        academicPeriodId = periodId,
                        value = value,
                        weight = weight
                    )
                }
                else -> item
            }

            resolvedItems.add(resolvedItem)
        }

        if (clarificationMessages.isNotEmpty()) {
            return structured.copy(
                type = AiActionType.CLARIFICATION,
                message = clarificationMessages.joinToString(" "),
                requiresConfirmation = false,
                missingFields = missingFields.distinct() + "materia_ambigua",
                items = resolvedItems
            )
        }

        if (missingFields.isNotEmpty()) {
            return structured.copy(
                type = AiActionType.CLARIFICATION,
                message = "Para continuar, necesito la siguiente información: ${missingFields.distinct().joinToString(", ")}.",
                requiresConfirmation = false,
                missingFields = missingFields.distinct(),
                items = resolvedItems
            )
        }

        return structured.copy(
            items = resolvedItems,
            requiresConfirmation = true
        )
    }

    suspend fun execute(pendingAction: AiPendingAction): Result<String> {
        val userId = getCurrentUidUseCase() ?: return Result.failure(IllegalStateException("Usuario no autenticado"))
        val response = pendingAction.structuredResponse
        val nowIso = Instant.now().toString()

        if (response.items.isEmpty()) {
            return Result.failure(IllegalArgumentException("No hay elementos para procesar."))
        }

        val successNames = mutableListOf<String>()
        var executedCount = 0

        for (item in response.items) {
            try {
                when (response.type) {
                    AiActionType.CREATE_SUBJECT -> {
                        val name = item.name ?: item.title ?: continue
                        val studyId = item.studyId ?: continue
                        val periodId = item.academicPeriodId ?: continue

                        val subject = Subject(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            studyId = studyId,
                            academicPeriodId = periodId,
                            name = name,
                            code = item.code,
                            credits = item.credits ?: 3,
                            professor = item.professor,
                            color = item.color ?: "#3F51B5",
                            notes = item.notes,
                            isCompleted = false,
                            createdAt = nowIso,
                            updatedAt = nowIso
                        )
                        saveSubjectUseCase(subject)
                        successNames.add(name)
                        executedCount++
                    }
                    AiActionType.CREATE_TASK -> {
                        val title = item.title ?: item.name ?: continue
                        val priority = try {
                            TaskPriority.valueOf(item.priority ?: "MEDIUM")
                        } catch (_: Exception) {
                            TaskPriority.MEDIUM
                        }

                        val task = Task(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            academicPeriodId = item.academicPeriodId,
                            subjectId = item.subjectId,
                            title = title,
                            description = item.description ?: item.notes,
                            dueAt = item.dueAt,
                            priority = priority,
                            status = TaskStatus.PENDING,
                            reminderType = TaskReminderType.HOURS_BEFORE,
                            reminderValue = 1,
                            isDeadlineReminderEnabled = true,
                            createdAt = nowIso,
                            updatedAt = nowIso
                        )
                        saveTaskUseCase(task)
                        successNames.add(title)
                        executedCount++
                    }
                    AiActionType.CREATE_EVENT -> {
                        val title = item.title ?: item.name ?: continue
                        val startAt = item.startAt ?: nowIso
                        val endAt = item.endAt ?: addOneHour(startAt)
                        val locType = try {
                            LocationType.valueOf(item.locationType ?: if (item.meetingUrl != null) "REMOTE" else "PHYSICAL")
                        } catch (_: Exception) {
                            if (item.meetingUrl != null) LocationType.REMOTE else LocationType.PHYSICAL
                        }

                        val event = Event(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            academicPeriodId = item.academicPeriodId,
                            subjectId = item.subjectId,
                            locationId = null,
                            recurrenceRuleId = null,
                            title = title,
                            startAt = startAt,
                            endAt = endAt,
                            locationType = locType,
                            eventType = EventType.CLASS,
                            meetingUrl = item.meetingUrl,
                            notes = item.notes,
                            reminders = emptyList(),
                            createdAt = nowIso,
                            updatedAt = nowIso
                        )
                        saveEventUseCase(event)
                        successNames.add(title)
                        executedCount++
                    }
                    AiActionType.REGISTER_GRADE -> {
                        val value = item.value ?: continue
                        val subjectId = item.subjectId ?: continue
                        val name = item.name ?: item.title ?: "Nota"

                        val grade = Grade(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            subjectId = subjectId,
                            academicPeriodId = item.academicPeriodId ?: "",
                            name = name,
                            value = value,
                            weight = item.weight ?: 0.0,
                            notes = item.notes,
                            createdAt = nowIso,
                            updatedAt = nowIso
                        )
                        saveGradeUseCase(grade)
                        successNames.add("$name ($value)")
                        executedCount++
                    }
                    else -> {}
                }
            } catch (_: Exception) {
            }
        }

        if (executedCount == 0) {
            return Result.failure(Exception("No se pudo ejecutar ninguna de las operaciones solicitadas."))
        }

        val actionTypeName = when (response.type) {
            AiActionType.CREATE_SUBJECT -> "materia(s)"
            AiActionType.CREATE_TASK -> "tarea(s)"
            AiActionType.CREATE_EVENT -> "evento(s)"
            AiActionType.REGISTER_GRADE -> "nota(s)"
            else -> "operación(es)"
        }

        return Result.success("✓ Se registraron $executedCount $actionTypeName exitosamente: ${successNames.joinToString(", ")}.")
    }

    private fun normalizeText(text: String): String {
        val nfd = Normalizer.normalize(text.lowercase().trim(), Normalizer.Form.NFD)
        return nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .replace(Regex("[^a-z0-9\\s]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun resolveDateTime(dateStr: String?, isEnd: Boolean, defaultHour: Int, defaultMinute: Int): String {
        val today = LocalDate.now(ZoneId.systemDefault())
        if (dateStr.isNullOrBlank()) {
            val targetDate = if (isEnd) today else today.plusDays(1)
            val timeStr = String.format(Locale.US, "%02d:%02d", defaultHour, defaultMinute)
            return "${targetDate} $timeStr"
        }

        val norm = normalizeText(dateStr)
        val targetDate = when {
            norm.contains("manana") -> today.plusDays(1)
            norm.contains("hoy") -> today
            norm.contains("lunes") -> getNextDayOfWeek(today, 1)
            norm.contains("martes") -> getNextDayOfWeek(today, 2)
            norm.contains("miercoles") -> getNextDayOfWeek(today, 3)
            norm.contains("jueves") -> getNextDayOfWeek(today, 4)
            norm.contains("viernes") -> getNextDayOfWeek(today, 5)
            norm.contains("sabado") -> getNextDayOfWeek(today, 6)
            norm.contains("domingo") -> getNextDayOfWeek(today, 7)
            else -> null
        }

        if (targetDate != null) {
            val extractedTime = extractTime(dateStr) ?: String.format(Locale.US, "%02d:%02d", defaultHour, defaultMinute)
            return "$targetDate $extractedTime"
        }

        return dateStr
    }

    private fun getNextDayOfWeek(today: LocalDate, targetDayOfWeek: Int): LocalDate {
        var date = today
        while (date.dayOfWeek.value != targetDayOfWeek) {
            date = date.plusDays(1)
        }
        if (date == today) {
            date = date.plusDays(7)
        }
        return date
    }

    private fun extractTime(text: String): String? {
        val timeRegex = Regex("(\\d{1,2}):(\\d{2})")
        val match = timeRegex.find(text)
        if (match != null) {
            val h = match.groupValues[1].toIntOrNull() ?: 0
            val m = match.groupValues[2].toIntOrNull() ?: 0
            return String.format(Locale.US, "%02d:%02d", h, m)
        }
        return null
    }

    private fun addOneHour(dateTimeStr: String): String {
        return try {
            if (dateTimeStr.contains(" ")) {
                val parts = dateTimeStr.split(" ")
                val date = parts[0]
                val timeParts = parts[1].split(":")
                val h = (timeParts[0].toIntOrNull() ?: 8) + 1
                val m = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                "$date ${String.format(Locale.US, "%02d:%02d", h % 24, m)}"
            } else {
                dateTimeStr
            }
        } catch (_: Exception) {
            dateTimeStr
        }
    }
}
