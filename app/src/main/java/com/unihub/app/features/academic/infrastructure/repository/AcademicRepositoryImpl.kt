package com.unihub.app.features.academic.infrastructure.repository

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.infrastructure.data.local.dao.AcademicDao
import com.unihub.app.features.academic.infrastructure.data.local.dao.StudyDao
import com.unihub.app.features.academic.infrastructure.data.mapper.toDomain
import com.unihub.app.features.academic.infrastructure.data.mapper.toEntity
import com.unihub.app.features.subjects.infrastructure.data.local.dao.SubjectDao
import com.unihub.app.features.subjects.infrastructure.data.mapper.toDomain as toSubjectDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademicRepositoryImpl @Inject constructor(
    private val academicDao: AcademicDao,
    private val subjectDao: SubjectDao,
    private val studyDao: StudyDao
) : AcademicRepository {

    override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> =
        academicDao.getAcademicPeriods(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> =
        academicDao.getAcademicPeriodsByStudy(userId, studyId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveAcademicPeriod(period: AcademicPeriod) {
        academicDao.insertPeriod(period.toEntity())
    }

    override suspend fun updateAcademicPeriod(period: AcademicPeriod) {
        academicDao.insertPeriod(period.toEntity())
    }

    override suspend fun deleteAcademicPeriod(id: String) {
        academicDao.deletePeriod(id)
    }

    override suspend fun setCurrentPeriod(userId: String, id: String) {
        academicDao.setCurrentPeriod(userId, id)
    }

    override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> =
        academicDao.getGradesBySubject(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveGrade(grade: Grade) {
        academicDao.insertGrade(grade.toEntity())
    }

    override suspend fun updateGrade(grade: Grade) {
        academicDao.insertGrade(grade.toEntity())
    }

    override suspend fun deleteGrade(id: String) {
        academicDao.deleteGrade(id)
    }

    override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> {
        return combine(
            academicDao.getAllGrades(userId),
            subjectDao.getAllSubjects(userId),
            studyDao.getStudyById(studyId)
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
                val weight = subjectGrades.sumOf { it.weight }
                val value = subjectGrades.sumOf { it.value * it.weight }
                val avg = if (weight > 0.0) value / weight else 0.0
                avg >= 3.0
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
                val currentTotalWeight = filteredGrades.sumOf { it.weight }
                val currentWeightedSum = filteredGrades.sumOf { it.value * it.weight }
                val currentSemesterAvg = if (currentTotalWeight > 0.0) currentWeightedSum / currentTotalWeight else 0.0

                val subjectsSum = filteredGrades.groupBy { it.subjectId }.map { (_, sGrades) ->
                    val sWeight = sGrades.sumOf { it.weight }
                    val sSum = sGrades.sumOf { it.value * sWeight }
                    if (sWeight > 0.0) sSum / sWeight else 0.0
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

    override suspend fun syncAcademicData(userId: String) {}
}
