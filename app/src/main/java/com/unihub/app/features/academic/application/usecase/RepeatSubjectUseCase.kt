package com.unihub.app.features.academic.application.usecase

import android.util.Log
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.academic.infrastructure.data.local.datasource.AcademicLocalDataSource
import com.unihub.app.features.academic.infrastructure.data.remote.datasource.AcademicRemoteDataSource
import com.unihub.app.features.auth.infrastructure.data.remote.FirebaseAuthDataSource
import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.local.datasource.EventLocalDataSource
import com.unihub.app.features.events.infrastructure.data.remote.datasource.EventRemoteDataSource
import com.unihub.app.features.notifications.application.usecase.CancelReminderUseCase
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.infrastructure.data.local.datasource.TaskLocalDataSource
import com.unihub.app.features.tasks.infrastructure.data.remote.datasource.TaskRemoteDataSource
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import javax.inject.Inject

class RepeatSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val academicLocalDataSource: AcademicLocalDataSource,
    private val academicRemoteDataSource: AcademicRemoteDataSource,
    private val taskLocalDataSource: TaskLocalDataSource,
    private val taskRemoteDataSource: TaskRemoteDataSource,
    private val taskDao: TaskDao,
    private val eventLocalDataSource: EventLocalDataSource,
    private val eventRemoteDataSource: EventRemoteDataSource,
    private val eventDao: EventDao,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val authDataSource: FirebaseAuthDataSource
) {
    suspend operator fun invoke(userId: String, subjectId: String) {
        val effectiveUid = authDataSource.getCurrentUid() ?: userId

        // 1. Delete Grades
        try {
            val localGrades = academicLocalDataSource.getGradesBySubjectOnce(subjectId)
            localGrades.forEach { grade ->
                academicLocalDataSource.deleteGrade(grade.id)
                try {
                    academicRemoteDataSource.deleteGrade(effectiveUid, grade.id)
                } catch (e: Exception) {
                    Log.w("RepeatSubjectUseCase", "Failed deleting grade ${grade.id} from Firestore: ${e.message}")
                }
                if (grade.userId.isNotBlank() && grade.userId != effectiveUid) {
                    try { academicRemoteDataSource.deleteGrade(grade.userId, grade.id) } catch (_: Exception) {}
                }
            }
            academicLocalDataSource.deleteGradesBySubject(subjectId)

            // Also clean up any lingering grades in Firestore for this subject
            val remoteGrades = academicRemoteDataSource.getGrades(effectiveUid).filter { it.subjectId == subjectId }
            remoteGrades.forEach { rg ->
                try {
                    academicRemoteDataSource.deleteGrade(effectiveUid, rg.id)
                } catch (e: Exception) {
                    Log.w("RepeatSubjectUseCase", "Failed deleting remote grade ${rg.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("RepeatSubjectUseCase", "Error clearing grades for subject $subjectId: ${e.message}")
        }

        // 2. Delete Tasks
        try {
            val localTasks = taskLocalDataSource.getTasksBySubjectOnce(subjectId)
            localTasks.forEach { task ->
                cancelReminderUseCase(task.id)
                taskDao.deleteTaskTagsForTask(task.id)
                taskLocalDataSource.deleteTask(task.id)
                try {
                    taskRemoteDataSource.deleteTask(effectiveUid, task.id)
                } catch (e: Exception) {
                    Log.w("RepeatSubjectUseCase", "Failed deleting task ${task.id} from Firestore: ${e.message}")
                }
                if (task.userId.isNotBlank() && task.userId != effectiveUid) {
                    try { taskRemoteDataSource.deleteTask(task.userId, task.id) } catch (_: Exception) {}
                }
            }
            taskLocalDataSource.deleteTasksBySubject(subjectId)

            // Also clean up any lingering tasks in Firestore for this subject
            val remoteTasks = taskRemoteDataSource.getTasks(effectiveUid).filter { it.subjectId == subjectId }
            remoteTasks.forEach { rt ->
                try {
                    taskRemoteDataSource.deleteTask(effectiveUid, rt.id)
                } catch (e: Exception) {
                    Log.w("RepeatSubjectUseCase", "Failed deleting remote task ${rt.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("RepeatSubjectUseCase", "Error clearing tasks for subject $subjectId: ${e.message}")
        }

        // 3. Delete Events
        try {
            val localEvents = eventLocalDataSource.getEventsBySubjectOnce(subjectId)
            localEvents.forEach { event ->
                cancelReminderUseCase(event.id)
                eventLocalDataSource.deleteRemindersForEvent(event.id)
                eventDao.deleteEventTagsForEvent(event.id)
                event.recurrenceRuleId?.let { ruleId ->
                    eventLocalDataSource.deleteRecurrenceDaysByRule(ruleId)
                    eventLocalDataSource.deleteRecurrenceRule(ruleId)
                }
                eventLocalDataSource.deleteEvent(event.id)
                try {
                    eventRemoteDataSource.deleteEvent(effectiveUid, event.id)
                } catch (e: Exception) {
                    Log.w("RepeatSubjectUseCase", "Failed deleting event ${event.id} from Firestore: ${e.message}")
                }
                if (event.userId.isNotBlank() && event.userId != effectiveUid) {
                    try { eventRemoteDataSource.deleteEvent(event.userId, event.id) } catch (_: Exception) {}
                }
            }
            eventLocalDataSource.deleteEventsBySubject(subjectId)

            // Also clean up any lingering events in Firestore for this subject
            val remoteEvents = eventRemoteDataSource.getEvents(effectiveUid).filter { it.subjectId == subjectId }
            remoteEvents.forEach { re ->
                try {
                    eventRemoteDataSource.deleteEvent(effectiveUid, re.id)
                } catch (e: Exception) {
                    Log.w("RepeatSubjectUseCase", "Failed deleting remote event ${re.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("RepeatSubjectUseCase", "Error clearing events for subject $subjectId: ${e.message}")
        }

        // 4. Reset Subject isCompleted = false
        try {
            val currentSubject = subjectRepository.getSubjectById(subjectId).firstOrNull()
            if (currentSubject != null) {
                val reset = currentSubject.copy(
                    isCompleted = false,
                    updatedAt = LocalDateTime.now().toString()
                )
                subjectRepository.updateSubject(reset)
            }
        } catch (e: Exception) {
            Log.e("RepeatSubjectUseCase", "Error resetting subject $subjectId: ${e.message}")
        }
    }
}
