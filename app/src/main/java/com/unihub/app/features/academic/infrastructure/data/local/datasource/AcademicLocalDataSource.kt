package com.unihub.app.features.academic.infrastructure.data.local.datasource

import com.unihub.app.features.academic.infrastructure.data.local.dao.AcademicDao
import com.unihub.app.features.academic.infrastructure.data.local.entity.AcademicPeriodEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademicLocalDataSource @Inject constructor(
    private val academicDao: AcademicDao
) {
    fun getPeriodById(id: String): Flow<AcademicPeriodEntity?> =
        academicDao.getPeriodById(id)

    fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriodEntity>> =
        academicDao.getAcademicPeriods(userId)

    fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriodEntity>> =
        academicDao.getAcademicPeriodsByStudy(userId, studyId)

    suspend fun insertPeriod(period: AcademicPeriodEntity) = academicDao.insertPeriod(period)

    suspend fun deletePeriod(id: String) = academicDao.deletePeriod(id)

    suspend fun setCurrentPeriod(userId: String, id: String) = academicDao.setCurrentPeriod(userId, id)

    fun getGradeById(id: String): Flow<GradeEntity?> =
        academicDao.getGradeById(id)

    fun getGradesBySubject(subjectId: String): Flow<List<GradeEntity>> =
        academicDao.getGradesBySubject(subjectId)

    fun getAllGrades(userId: String): Flow<List<GradeEntity>> =
        academicDao.getAllGrades(userId)

    suspend fun insertGrade(grade: GradeEntity) = academicDao.insertGrade(grade)

    suspend fun deleteGrade(id: String) = academicDao.deleteGrade(id)

    suspend fun getGradesBySubjectOnce(subjectId: String): List<GradeEntity> = academicDao.getGradesBySubjectOnce(subjectId)

    suspend fun deleteGradesBySubject(subjectId: String) = academicDao.deleteGradesBySubject(subjectId)
}
