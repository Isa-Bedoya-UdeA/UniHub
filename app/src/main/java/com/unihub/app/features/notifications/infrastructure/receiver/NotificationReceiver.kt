package com.unihub.app.features.notifications.infrastructure.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.unihub.app.MainActivity
import com.unihub.app.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = intent.getStringExtra("EXTRA_ID") ?: return
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "UniHub"
        val content = intent.getStringExtra("EXTRA_CONTENT") ?: ""
        val channelId = intent.getStringExtra("EXTRA_CHANNEL_ID") ?: "EVENT_REMINDERS"
        val type = intent.getStringExtra("EXTRA_TYPE") ?: ""
        val entityId = intent.getStringExtra("EXTRA_ENTITY_ID") ?: id

        createNotificationChannel(notificationManager, channelId)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NAVIGATE_TO", type)
            putExtra("ENTITY_ID", entityId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel != null) return

            val name = when (channelId) {
                "EVENT_REMINDERS" -> "Event Reminders"
                "TASK_REMINDERS" -> "Task Reminders"
                "DEADLINE_REMINDERS" -> "Deadline Reminders"
                else -> "Notifications"
            }
            val descriptionText = "Notifications for UniHub reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
