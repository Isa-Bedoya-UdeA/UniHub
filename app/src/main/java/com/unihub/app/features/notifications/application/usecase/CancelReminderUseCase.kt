package com.unihub.app.features.notifications.application.usecase

import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import javax.inject.Inject

class CancelReminderUseCase @Inject constructor(
    private val notificationScheduler: NotificationScheduler
) {
    operator fun invoke(entityId: String) {
        notificationScheduler.cancelAllForEntity(entityId)
    }
}
