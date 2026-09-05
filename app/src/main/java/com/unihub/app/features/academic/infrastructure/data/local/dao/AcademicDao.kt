package com.unihub.app.features.academic.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.academic.infrastructure.data.local.entity.AcademicPeriodEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {
    @Query("SELECT * FROM AcademicPeriod WHERE user_id = :userId")
    fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: AcademicPeriodEntity)

    @Query("SELECT * FROM Grade WHERE subject_id = :subjectId")
    fun getGradesBySubject(subjectId: String): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity)

    @Query("DELETE FROM Grade WHERE grade_id = :id")
    suspend fun deleteGrade(id: String)
}
