package com.unihub.app.features.notifications.domain.repository

import com.unihub.app.features.notifications.domain.model.Reminder

interface NotificationScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminderId: String)
    fun cancelAllForEntity(entityId: String)
    fun hasExactAlarmPermission(): Boolean
}
