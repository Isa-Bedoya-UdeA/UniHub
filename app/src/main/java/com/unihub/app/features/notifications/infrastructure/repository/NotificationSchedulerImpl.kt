package com.unihub.app.features.notifications.infrastructure.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.unihub.app.core.util.DateUtils
import com.unihub.app.features.notifications.domain.model.Reminder
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.notifications.infrastructure.receiver.NotificationReceiver
import java.time.ZoneId

class NotificationSchedulerImpl(
    private val context: Context,
    private val alarmManager: AlarmManager
) : NotificationScheduler {

    override fun schedule(reminder: Reminder) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_ID", reminder.id)
            putExtra("EXTRA_TITLE", reminder.title)
            putExtra("EXTRA_CONTENT", reminder.content)
            putExtra("EXTRA_CHANNEL_ID", reminder.channelId)
            putExtra("EXTRA_TYPE", reminder.type.name)
            putExtra("EXTRA_ENTITY_ID", reminder.entityId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = try {
            val parsed = DateUtils.parseDateTime(reminder.dateTime)
            parsed.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            return
        }

        if (triggerAtMillis <= System.currentTimeMillis()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(reminderId: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun cancelAllForEntity(entityId: String) {
        cancel("EVENT_$entityId")
        cancel("TASK_$entityId")
        cancel("DEADLINE_$entityId")
    }

    override fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
