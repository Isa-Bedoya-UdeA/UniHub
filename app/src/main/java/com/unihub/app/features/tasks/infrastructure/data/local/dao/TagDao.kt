package com.unihub.app.features.tasks.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM Tag WHERE user_id = :userId")
    fun getTagsByUser(userId: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM Tag WHERE tag_id = :id")
    fun getTagById(id: String): Flow<TagEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("DELETE FROM Tag WHERE tag_id = :id")
    suspend fun deleteTagById(id: String)
}
