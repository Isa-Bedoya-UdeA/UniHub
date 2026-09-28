package com.unihub.app.features.tasks.infrastructure.data.local.datasource

import com.unihub.app.features.tasks.infrastructure.data.local.dao.TagDao
import com.unihub.app.features.tasks.infrastructure.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagLocalDataSource @Inject constructor(
    private val tagDao: TagDao
) {
    fun getTagsByUser(userId: String): Flow<List<TagEntity>> =
        tagDao.getTagsByUser(userId)

    fun getTagById(id: String): Flow<TagEntity?> =
        tagDao.getTagById(id)

    suspend fun insertTag(tag: TagEntity) =
        tagDao.insertTag(tag)

    suspend fun deleteTagById(id: String) =
        tagDao.deleteTagById(id)
}
