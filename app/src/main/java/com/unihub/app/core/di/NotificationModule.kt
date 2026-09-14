package com.unihub.app.core.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.unihub.app.features.notifications.domain.repository.NotificationScheduler
import com.unihub.app.features.notifications.infrastructure.repository.NotificationSchedulerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        @ApplicationContext context: Context,
        alarmManager: AlarmManager
    ): NotificationScheduler {
        return NotificationSchedulerImpl(context, alarmManager)
    }
}
