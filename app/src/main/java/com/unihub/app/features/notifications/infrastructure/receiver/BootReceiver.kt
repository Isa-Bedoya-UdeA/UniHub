package com.unihub.app.features.notifications.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.unihub.app.features.notifications.application.usecase.RescheduleRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var rescheduleRemindersUseCase: RescheduleRemindersUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    rescheduleRemindersUseCase()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
