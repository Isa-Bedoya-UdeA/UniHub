package com.unihub.app.features.subjects.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.subjects.infrastructure.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM Subject WHERE user_id = :userId AND academic_period_id = :periodId")
    fun getSubjects(userId: String, periodId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM Subject WHERE user_id = :userId")
    fun getAllSubjects(userId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM Subject WHERE subject_id = :id")
    fun getSubjectById(id: String): Flow<SubjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Query("DELETE FROM Subject WHERE subject_id = :id")
    suspend fun deleteSubject(id: String)
}
