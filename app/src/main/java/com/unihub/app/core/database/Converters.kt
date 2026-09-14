package com.unihub.app.core.database

import androidx.room.TypeConverter
import com.unihub.app.features.events.domain.model.EventType
import com.unihub.app.features.events.domain.model.LocationType
import com.unihub.app.features.events.domain.model.ReminderType
import com.unihub.app.features.tasks.domain.model.TaskPriority
import com.unihub.app.features.tasks.domain.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromLocationType(value: LocationType): String = value.name
    @TypeConverter
    fun toLocationType(value: String): LocationType = enumValueOf(value)

    @TypeConverter
    fun fromEventType(value: EventType): String = value.name
    @TypeConverter
    fun toEventType(value: String): EventType = enumValueOf(value)

    @TypeConverter
    fun fromReminderType(value: ReminderType): String = value.name
    @TypeConverter
    fun toReminderType(value: String): ReminderType = enumValueOf(value)

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name
    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = enumValueOf(value)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name
    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = enumValueOf(value)
}
