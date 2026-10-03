package com.unihub.app.core.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val subjectRepository: SubjectRepository,
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository,
    private val academicRepository: AcademicRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val userId = authRepository.getCurrentUid()
            if (userId == null) {
                Log.w(TAG, "No authenticated user, skipping sync")
                return@withContext Result.success()
            }

            return@withContext try {
                subjectRepository.syncSubjects(userId)
                eventRepository.syncEvents(userId)
                taskRepository.syncTasks(userId)
                academicRepository.syncAcademicData(userId)
                Log.i(TAG, "Sync completed successfully for user: $userId")
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed: ${e.message}")
                Result.retry()
            }
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        const val SYNC_WORK_NAME = "unihub_sync_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        }
    }
}
