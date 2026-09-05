package com.unihub.app.features.academic.infrastructure.repository

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademicRepositoryImpl @Inject constructor() : AcademicRepository {

    private val mockPeriods = MutableStateFlow<List<AcademicPeriod>>(
        listOf(
            AcademicPeriod("1", "user123", "2026-2", "01-08-2026", "15-12-2026", true, "", "")
        )
    )
    
    private val mockGrades = MutableStateFlow<List<Grade>>(
        listOf(
            Grade("g1", "user123", "1", "1", "Parcial 1", 4.5, 0.3, null, "", ""),
            Grade("g2", "user123", "1", "1", "Taller 1", 4.0, 0.2, null, "", "")
        )
    )

    override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> =
        mockPeriods.map { it.filter { p -> p.userId == userId } }

    override suspend fun saveAcademicPeriod(period: AcademicPeriod) {
        val current = mockPeriods.value.toMutableList()
        val index = current.indexOfFirst { it.id == period.id }
        if (index != -1) current[index] = period else current.add(period)
        mockPeriods.emit(current)
    }

    override suspend fun updateAcademicPeriod(period: AcademicPeriod) {
        saveAcademicPeriod(period)
    }

    override suspend fun deleteAcademicPeriod(id: String) {
        mockPeriods.emit(mockPeriods.value.filter { it.id != id })
    }

    override suspend fun setCurrentPeriod(id: String) {
        val current = mockPeriods.value.map { 
            it.copy(isCurrent = it.id == id)
        }
        mockPeriods.emit(current)
    }

    override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> =
        mockGrades.map { it.filter { g -> g.subjectId == subjectId } }

    override suspend fun saveGrade(grade: Grade) {
        val current = mockGrades.value.toMutableList()
        val index = current.indexOfFirst { it.id == grade.id }
        if (index != -1) current[index] = grade else current.add(grade)
        mockGrades.emit(current)
    }

    override suspend fun updateGrade(grade: Grade) {
        saveGrade(grade)
    }

    override suspend fun deleteGrade(id: String) {
        mockGrades.emit(mockGrades.value.filter { it.id != id })
    }

    override fun getAcademicSummary(userId: String): Flow<AcademicSummary> {
        return mockGrades.map { grades ->
            val userGrades = grades.filter { it.userId == userId }
            if (userGrades.isEmpty()) {
                AcademicSummary(0.0, 0.0, 0, 0.0)
            } else {
                // Correct Weighted Average Calculation
                val totalWeight = userGrades.sumOf { it.weight }
                val weightedSum = userGrades.sumOf { it.value * it.weight }
                
                // If total weight < 1.0, we assume the rest of the 100% hasn't been graded yet.
                // Professional standard: Current GPA is based on what HAS been graded.
                val avg = if (totalWeight > 0.0) weightedSum / totalWeight else 0.0
                
                // Cumulative GPA across all subjects
                val subjectsSum = userGrades.groupBy { it.subjectId }.map { (_, subjectGrades) ->
                    val sWeight = subjectGrades.sumOf { it.weight }
                    val sSum = subjectGrades.sumOf { it.value * it.weight }
                    if (sWeight > 0.0) sSum / sWeight else 0.0
                }
                val cumulativeGpa = if (subjectsSum.isNotEmpty()) subjectsSum.average() else 0.0

                AcademicSummary(
                    cumulativeGpa = cumulativeGpa,
                    currentSemesterGpa = avg,
                    totalCredits = 18, // Mock total
                    progressPercentage = (totalWeight * 100).coerceIn(0.0, 100.0)
                )
            }
        }
    }

    override suspend fun syncAcademicData(userId: String) {}
}
