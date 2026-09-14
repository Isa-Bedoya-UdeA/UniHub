package com.unihub.app.features.academic.domain.repository

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>>
    fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>>
    suspend fun saveAcademicPeriod(period: AcademicPeriod)
    suspend fun updateAcademicPeriod(period: AcademicPeriod)
    suspend fun deleteAcademicPeriod(id: String)
    suspend fun setCurrentPeriod(userId: String, id: String)

    fun getGradesBySubject(subjectId: String): Flow<List<Grade>>
    suspend fun saveGrade(grade: Grade)
    suspend fun updateGrade(grade: Grade)
    suspend fun deleteGrade(id: String)

    fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary>

    suspend fun syncAcademicData(userId: String)
}
