package com.unihub.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unihub.app.features.academic.infrastructure.data.local.dao.AcademicDao
import com.unihub.app.features.academic.infrastructure.data.local.dao.StudyDao
import com.unihub.app.features.academic.infrastructure.data.local.entity.AcademicPeriodEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.GradeEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.StudyEntity
import com.unihub.app.features.auth.infrastructure.data.local.dao.UserDao
import com.unihub.app.features.auth.infrastructure.data.local.entity.UserEntity
import com.unihub.app.features.events.infrastructure.data.local.dao.EventDao
import com.unihub.app.features.events.infrastructure.data.local.dao.EventReminderDao
import com.unihub.app.features.events.infrastructure.data.local.entity.EventEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.EventReminderEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.EventTagEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceDayEntity
import com.unihub.app.features.events.infrastructure.data.local.entity.RecurrenceRuleEntity
import com.unihub.app.features.location.infrastructure.data.local.dao.LocationDao
import com.unihub.app.features.location.infrastructure.data.local.entity.LocationEntity
import com.unihub.app.features.settings.infrastructure.data.local.dao.UserPreferencesDao
import com.unihub.app.features.settings.infrastructure.data.local.entity.UserPreferencesEntity
import com.unihub.app.features.subjects.infrastructure.data.local.dao.SubjectDao
import com.unihub.app.features.subjects.infrastructure.data.local.entity.SubjectEntity
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TagDao
import com.unihub.app.features.tasks.infrastructure.data.local.dao.TaskDao
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TagEntity
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TaskEntity
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TaskTagEntity

@Database(
    entities = [
        UserEntity::class,
        StudyEntity::class,
        AcademicPeriodEntity::class,
        SubjectEntity::class,
        LocationEntity::class,
        EventEntity::class,
        EventReminderEntity::class,
        RecurrenceRuleEntity::class,
        RecurrenceDayEntity::class,
        TaskEntity::class,
        GradeEntity::class,
        TagEntity::class,
        EventTagEntity::class,
        TaskTagEntity::class,
        UserPreferencesEntity::class
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studyDao(): StudyDao
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun eventReminderDao(): EventReminderDao
    abstract fun locationDao(): LocationDao
    abstract fun academicDao(): AcademicDao
    abstract fun tagDao(): TagDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Study ADD COLUMN approved_credits INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE Study ADD COLUMN cumulative_gpa REAL DEFAULT NULL")
            }
        }
    }
}
