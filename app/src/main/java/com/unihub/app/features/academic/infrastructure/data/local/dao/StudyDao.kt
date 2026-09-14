package com.unihub.app.features.academic.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.academic.infrastructure.data.local.entity.StudyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM Study WHERE user_id = :userId")
    fun getStudies(userId: String): Flow<List<StudyEntity>>

    @Query("SELECT * FROM Study WHERE study_id = :id")
    fun getStudyById(id: String): Flow<StudyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudy(study: StudyEntity)

    @Query("UPDATE Study SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivateAllStudies(userId: String)

    @Transaction
    suspend fun setActiveStudy(userId: String, studyId: String) {
        deactivateAllStudies(userId)
        markActive(studyId)
    }

    @Query("UPDATE Study SET is_active = 1 WHERE study_id = :studyId")
    suspend fun markActive(studyId: String)

    @Query("DELETE FROM Study WHERE study_id = :id")
    suspend fun deleteStudy(id: String)
}
