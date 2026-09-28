package com.unihub.app.features.academic.infrastructure.repository

import android.util.Log
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.infrastructure.data.local.datasource.AcademicLocalDataSource
import com.unihub.app.features.academic.infrastructure.data.local.datasource.StudyLocalDataSource
import com.unihub.app.features.academic.infrastructure.data.mapper.toDomain
import com.unihub.app.features.academic.infrastructure.data.mapper.toDto
import com.unihub.app.features.academic.infrastructure.data.mapper.toEntity
import com.unihub.app.features.academic.infrastructure.data.remote.datasource.AcademicRemoteDataSource
import com.unihub.app.features.subjects.infrastructure.data.local.dao.SubjectDao
import com.unihub.app.features.subjects.infrastructure.data.mapper.toDomain as toSubjectDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademicRepositoryImpl @Inject constructor(
    private val localDataSource: AcademicLocalDataSource,
    private val remoteDataSource: AcademicRemoteDataSource,
    private val subjectDao: SubjectDao,
    private val studyLocalDataSource: StudyLocalDataSource
) : AcademicRepository {

    override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> =
        localDataSource.getAcademicPeriods(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> =
        localDataSource.getAcademicPeriodsByStudy(userId, studyId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveAcademicPeriod(period: AcademicPeriod) {
        Log.d("FIRESTORE_DEBUG", "=== SAVE ACADEMIC PERIOD CALLED ===")
        Log.d("FIRESTORE_DEBUG", "Period ID: ${period.id}")
        Log.d("FIRESTORE_DEBUG", "Period Name: ${period.name}")
        Log.d("FIRESTORE_DEBUG", "User ID: ${period.userId}")
        
        localDataSource.insertPeriod(period.toEntity())
        Log.d("FIRESTORE_DEBUG", "Period saved to Room successfully")
        
        try {
            Log.d("FIRESTORE_DEBUG", "About to call remoteDataSource.saveAcademicPeriod()")
            val dto = period.toDto()
            Log.d("FIRESTORE_DEBUG", "DTO created: $dto")
            remoteDataSource.saveAcademicPeriod(dto)
            Log.d("FIRESTORE_DEBUG", "remoteDataSource.saveAcademicPeriod() completed successfully")
        } catch (e: Exception) {
            Log.e("FIRESTORE_DEBUG", "ERROR saving academic period to Firestore: ${e.message}", e)
            Log.e("FIRESTORE_DEBUG", "Exception type: ${e.javaClass.simpleName}")
            Log.e("FIRESTORE_DEBUG", "Stack trace: ${e.stackTraceToString()}")
        }
    }

    override suspend fun updateAcademicPeriod(period: AcademicPeriod) {
        saveAcademicPeriod(period)
    }

    override suspend fun deleteAcademicPeriod(id: String) {
        val period = localDataSource.getPeriodById(id).firstOrNull()
        localDataSource.deletePeriod(id)
        if (period != null) {
            try {
                remoteDataSource.deleteAcademicPeriod(period.userId, id)
            } catch (e: Exception) {
                Log.e("AcademicRepositoryImpl", "Error deleting academic period from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun setCurrentPeriod(userId: String, id: String) {
        localDataSource.setCurrentPeriod(userId, id)
    }

    override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> =
        localDataSource.getGradesBySubject(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveGrade(grade: Grade) {
        Log.d("FIRESTORE_DEBUG", "=== SAVE GRADE CALLED ===")
        Log.d("FIRESTORE_DEBUG", "Grade ID: ${grade.id}")
        Log.d("FIRESTORE_DEBUG", "Grade Name: ${grade.name}")
        Log.d("FIRESTORE_DEBUG", "User ID: ${grade.userId}")
        
        localDataSource.insertGrade(grade.toEntity())
        Log.d("FIRESTORE_DEBUG", "Grade saved to Room successfully")
        
        try {
            Log.d("FIRESTORE_DEBUG", "About to call remoteDataSource.saveGrade()")
            val dto = grade.toDto()
            Log.d("FIRESTORE_DEBUG", "DTO created: $dto")
            remoteDataSource.saveGrade(dto)
            Log.d("FIRESTORE_DEBUG", "remoteDataSource.saveGrade() completed successfully")
        } catch (e: Exception) {
            Log.e("FIRESTORE_DEBUG", "ERROR saving grade to Firestore: ${e.message}", e)
            Log.e("FIRESTORE_DEBUG", "Exception type: ${e.javaClass.simpleName}")
            Log.e("FIRESTORE_DEBUG", "Stack trace: ${e.stackTraceToString()}")
        }
    }

    override suspend fun updateGrade(grade: Grade) {
        saveGrade(grade)
    }

    override suspend fun deleteGrade(id: String) {
        val grade = localDataSource.getGradeById(id).firstOrNull()
        localDataSource.deleteGrade(id)
        if (grade != null) {
            try {
                remoteDataSource.deleteGrade(grade.userId, id)
            } catch (e: Exception) {
                Log.e("AcademicRepositoryImpl", "Error deleting grade from Firestore: ${e.message}")
            }
        }
    }

    override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> {
        return combine(
            localDataSource.getAllGrades(userId),
            subjectDao.getAllSubjects(userId),
            studyLocalDataSource.getStudyById(studyId)
        ) { gradeEntities, subjectEntities, studyEntity ->
            val userGrades = gradeEntities.map { it.toDomain() }
            val allSubjects = subjectEntities.map { it.toSubjectDomain() }
            val study = studyEntity?.toDomain()

            val totalTargetCredits = study?.totalCredits ?: 0
            val manualApprovedCredits = study?.approvedCredits ?: 0
            val manualCumulativeGpa = study?.cumulativeGpa ?: 0.0

            val filteredSubjects = allSubjects.filter { it.studyId == studyId }
            val filteredGrades = userGrades.filter { grade ->
                filteredSubjects.any { it.id == grade.subjectId }
            }

            val currentEarnedCredits = filteredSubjects.filter { subject ->
                val subjectGrades = filteredGrades.filter { it.subjectId == subject.id }
                if (subjectGrades.isEmpty()) return@filter false
                val value = subjectGrades.sumOf { it.value * it.weight }
                value >= 3.0
            }.sumOf { it.credits ?: 0 }

            val totalEarnedCredits = manualApprovedCredits + currentEarnedCredits

            if (filteredGrades.isEmpty() && manualApprovedCredits == 0) {
                AcademicSummary(
                    cumulativeGpa = 0.0,
                    currentSemesterGpa = 0.0,
                    earnedCredits = totalEarnedCredits,
                    targetCredits = totalTargetCredits,
                    progressPercentage = if (totalTargetCredits > 0) (totalEarnedCredits.toDouble() / totalTargetCredits.toDouble() * 100.0).coerceIn(0.0, 100.0) else 0.0,
                    hasManualData = manualApprovedCredits > 0
                )
            } else {
                val currentWeightedSum = filteredGrades.sumOf { it.value * it.weight }
                val currentSemesterAvg = currentWeightedSum

                val subjectsSum = filteredGrades.groupBy { it.subjectId }.map { (_, sGrades) ->
                    sGrades.sumOf { it.value * it.weight }
                }
                
                val currentGpa = if (subjectsSum.isNotEmpty()) subjectsSum.average() else 0.0

                val cumulativeGpa = if (manualApprovedCredits > 0 && manualCumulativeGpa > 0.0) {
                    val manualWeightedSum = manualCumulativeGpa * manualApprovedCredits
                    val currentWeightedSum = currentGpa * currentEarnedCredits
                    val totalWeight = manualApprovedCredits + currentEarnedCredits
                    if (totalWeight > 0) (manualWeightedSum + currentWeightedSum) / totalWeight else currentGpa
                } else {
                    currentGpa
                }

                AcademicSummary(
                    cumulativeGpa = cumulativeGpa,
                    currentSemesterGpa = currentSemesterAvg,
                    earnedCredits = totalEarnedCredits,
                    targetCredits = totalTargetCredits,
                    progressPercentage = if (totalTargetCredits > 0) (totalEarnedCredits.toDouble() / totalTargetCredits.toDouble() * 100.0).coerceIn(0.0, 100.0) else 0.0,
                    hasManualData = manualApprovedCredits > 0
                )
            }
        }
    }

    override suspend fun syncAcademicData(userId: String) {
        try {
            val remotePeriods = remoteDataSource.getAcademicPeriods(userId)
            remotePeriods.forEach { periodDto ->
                localDataSource.insertPeriod(periodDto.toDomain().toEntity())
            }
            val remoteGrades = remoteDataSource.getGrades(userId)
            remoteGrades.forEach { gradeDto ->
                localDataSource.insertGrade(gradeDto.toDomain().toEntity())
            }
        } catch (e: Exception) {
            Log.e("AcademicRepositoryImpl", "Error syncing academic data: ${e.message}")
        }
    }
}
