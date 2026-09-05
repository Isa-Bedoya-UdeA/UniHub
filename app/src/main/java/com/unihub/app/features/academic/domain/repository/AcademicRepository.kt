package com.unihub.app.features.academic.domain.repository

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    // Periods
    fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>>
    suspend fun saveAcademicPeriod(period: AcademicPeriod)
    suspend fun updateAcademicPeriod(period: AcademicPeriod)
    suspend fun deleteAcademicPeriod(id: String)
    suspend fun setCurrentPeriod(id: String)
    
    // Grades
    fun getGradesBySubject(subjectId: String): Flow<List<Grade>>
    suspend fun saveGrade(grade: Grade)
    suspend fun updateGrade(grade: Grade)
    suspend fun deleteGrade(id: String)
    
    // Summary
    fun getAcademicSummary(userId: String): Flow<AcademicSummary>
    
    // Sync
    suspend fun syncAcademicData(userId: String)
}
