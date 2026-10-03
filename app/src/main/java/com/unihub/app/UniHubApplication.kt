package com.unihub.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.libraries.places.api.Places
import com.unihub.app.core.sync.SyncManager
import com.unihub.app.features.notifications.application.usecase.RescheduleRemindersUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class UniHubApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var rescheduleRemindersUseCase: RescheduleRemindersUseCase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncManager: SyncManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        // Initialize Places BEFORE super.onCreate() because Hilt injection
        // (triggered by super.onCreate()) may create a PlacesClient that requires
        // Places to be initialized first.
        initializePlaces()
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            rescheduleRemindersUseCase()
        }

        syncManager.scheduleSync()
    }

    private fun initializePlaces() {
        try {
            val info = packageManager.getApplicationInfo(
                packageName,
                android.content.pm.PackageManager.GET_META_DATA
            )
            val apiKey = info.metaData?.getString("com.google.android.geo.API_KEY")
            
            if (!apiKey.isNullOrBlank() && !apiKey.startsWith("$")) {
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, apiKey)
                }
            }
        } catch (e: Exception) {
            // Silently handle Places initialization errors
        }
    }
}
