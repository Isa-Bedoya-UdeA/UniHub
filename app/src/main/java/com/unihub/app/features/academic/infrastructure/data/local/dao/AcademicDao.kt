package com.unihub.app.features.academic.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.academic.infrastructure.data.local.entity.AcademicPeriodEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {
    @Query("SELECT * FROM AcademicPeriod WHERE user_id = :userId")
    fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriodEntity>>

    @Query("SELECT * FROM AcademicPeriod WHERE user_id = :userId AND study_id = :studyId")
    fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: AcademicPeriodEntity)

    @Query("UPDATE AcademicPeriod SET is_current = 0 WHERE user_id = :userId")
    suspend fun deactivateAllPeriods(userId: String)

    @Query("UPDATE AcademicPeriod SET is_current = 1 WHERE academic_period_id = :id")
    suspend fun markPeriodCurrent(id: String)

    @Transaction
    suspend fun setCurrentPeriod(userId: String, id: String) {
        deactivateAllPeriods(userId)
        markPeriodCurrent(id)
    }

    @Query("DELETE FROM AcademicPeriod WHERE academic_period_id = :id")
    suspend fun deletePeriod(id: String)

    @Query("SELECT * FROM Grade WHERE subject_id = :subjectId")
    fun getGradesBySubject(subjectId: String): Flow<List<GradeEntity>>

    @Query("SELECT * FROM Grade WHERE user_id = :userId")
    fun getAllGrades(userId: String): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity)

    @Query("DELETE FROM Grade WHERE grade_id = :id")
    suspend fun deleteGrade(id: String)
}
