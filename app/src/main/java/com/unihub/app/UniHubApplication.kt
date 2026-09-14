package com.unihub.app

import android.app.Application
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
        
        CoroutineScope(Dispatchers.IO).launch {
            rescheduleRemindersUseCase()
        }
    }
}
