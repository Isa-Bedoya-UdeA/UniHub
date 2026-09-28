package com.unihub.app.core.sync

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleSync() {
        SyncWorker.schedule(context)
    }

    fun cancelSync() {
        SyncWorker.cancel(context)
    }
}
