package com.unihub.app

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.unihub.app.features.notifications.application.usecase.RescheduleRemindersUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class UniHubApplication : Application() {

    @Inject
    lateinit var rescheduleRemindersUseCase: RescheduleRemindersUseCase

    override fun onCreate() {
        super.onCreate()

        initializePlaces()

        CoroutineScope(Dispatchers.IO).launch {
            rescheduleRemindersUseCase()
        }
    }

    private fun initializePlaces() {
        try {
            val info = packageManager.getApplicationInfo(
                packageName,
                android.content.pm.PackageManager.GET_META_DATA
            )
            val apiKey = info.metaData.getString("com.google.android.geo.API_KEY")
            if (!apiKey.isNullOrBlank() && !apiKey.startsWith("$")) {
                Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
            }
        } catch (e: Exception) {
            android.util.Log.e("UniHubApplication", "Failed to initialize Places SDK", e)
        }
    }
}
