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
    version = 4,
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
                try {
                    db.execSQL("ALTER TABLE Study ADD COLUMN approved_credits INTEGER DEFAULT NULL")
                } catch (e: Exception) { }
                try {
                    db.execSQL("ALTER TABLE Study ADD COLUMN cumulative_gpa REAL DEFAULT NULL")
                } catch (e: Exception) { }
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE Task ADD COLUMN reminder_type TEXT DEFAULT NULL")
                } catch (e: Exception) { }
                try {
                    db.execSQL("ALTER TABLE Task ADD COLUMN reminder_value INTEGER DEFAULT NULL")
                } catch (e: Exception) { }
                db.execSQL("CREATE TABLE IF NOT EXISTS `Task_new` (`task_id` TEXT NOT NULL, `user_id` TEXT NOT NULL, `academic_period_id` TEXT DEFAULT NULL, `subject_id` TEXT DEFAULT NULL, `title` TEXT NOT NULL, `description` TEXT DEFAULT NULL, `due_at` TEXT DEFAULT NULL, `priority` TEXT NOT NULL, `status` TEXT NOT NULL, `reminder_type` TEXT DEFAULT NULL, `reminder_value` INTEGER DEFAULT NULL, `is_deadline_reminder_enabled` INTEGER NOT NULL DEFAULT 0, `created_at` TEXT NOT NULL, `updated_at` TEXT NOT NULL, PRIMARY KEY(`task_id`))")
                db.execSQL("INSERT INTO Task_new (task_id, user_id, academic_period_id, subject_id, title, description, due_at, priority, status, reminder_type, reminder_value, is_deadline_reminder_enabled, created_at, updated_at) SELECT task_id, user_id, academic_period_id, subject_id, title, description, due_at, priority, status, reminder_type, reminder_value, is_deadline_reminder_enabled, created_at, updated_at FROM Task")
                db.execSQL("DROP TABLE Task")
                db.execSQL("ALTER TABLE Task_new RENAME TO Task")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE Event ADD COLUMN event_type TEXT NOT NULL DEFAULT 'OTHER'")
                } catch (e: Exception) {
                    // Column already exists, ignore
                }
            }
        }
    }
}
