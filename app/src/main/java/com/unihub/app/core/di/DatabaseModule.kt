package com.unihub.app.core.di

import android.content.Context
import androidx.room.Room
import com.unihub.app.core.database.AppDatabase
import com.unihub.app.features.academic.infrastructure.data.local.dao.AcademicDao
import com.unihub.app.features.academic.infrastructure.data.local.dao.StudyDao
import com.unihub.app.features.auth.infrastructure.data.local.dao.UserDao
import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.local.dao.EventReminderDao
import com.unihub.app.features.location.infrastructure.data.local.dao.LocationDao
import com.unihub.app.features.settings.infrastructure.data.local.dao.UserPreferencesDao
import com.unihub.app.features.subjects.infrastructure.data.local.dao.SubjectDao
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TagDao
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unihub_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideEventReminderDao(database: AppDatabase): EventReminderDao = database.eventReminderDao()

    @Provides
    @Singleton
    fun provideLocationDao(database: AppDatabase): LocationDao = database.locationDao()

    @Provides
    @Singleton
    fun provideStudyDao(database: AppDatabase): StudyDao = database.studyDao()

    @Provides
    @Singleton
    fun provideAcademicDao(database: AppDatabase): AcademicDao = database.academicDao()

    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao = database.tagDao()

    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: AppDatabase): UserPreferencesDao = database.userPreferencesDao()
}
