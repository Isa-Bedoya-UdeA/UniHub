package com.unihub.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unihub.app.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    // DAOs will be added here as features are implemented
    // abstract fun userDao(): UserDao
}
