package com.unihub.app.features.tasks.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.tasks.domain.model.TaskStatus
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TaskEntity
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TaskTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task WHERE user_id = :userId")
    fun getTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM Task WHERE subject_id = :subjectId")
    fun getTasksBySubject(subjectId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM Task WHERE task_id = :id")
    fun getTaskById(id: String): Flow<TaskEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE Task SET status = :status WHERE task_id = :id")
    suspend fun updateStatus(id: String, status: TaskStatus)

    @Query("DELETE FROM Task WHERE task_id = :id")
    suspend fun deleteTask(id: String)

    // Task Tags
    @Query("SELECT * FROM TaskTag WHERE task_id = :taskId")
    fun getTagsByTask(taskId: String): Flow<List<TaskTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTag(taskTag: TaskTagEntity)

    @Query("DELETE FROM TaskTag WHERE task_id = :taskId AND tag_id = :tagId")
    suspend fun deleteTaskTag(taskId: String, tagId: String)
}
