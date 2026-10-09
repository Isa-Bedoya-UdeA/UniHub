package com.unihub.app.features.academic.application.usecase

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AcademicUseCasesTest {

    private val periodsStorage = mutableMapOf<String, AcademicPeriod>()
    private val gradesStorage = mutableMapOf<String, Grade>()

    private val fakeAcademicRepository = object : AcademicRepository {
        override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> =
            flowOf(periodsStorage.values.filter { it.userId == userId })

        override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> =
            flowOf(periodsStorage.values.filter { it.userId == userId && it.studyId == studyId })

        override suspend fun saveAcademicPeriod(period: AcademicPeriod) {
            periodsStorage[period.id] = period
        }

        override suspend fun updateAcademicPeriod(period: AcademicPeriod) {
            periodsStorage[period.id] = period
        }

        override suspend fun deleteAcademicPeriod(id: String) {
            periodsStorage.remove(id)
        }

        override suspend fun setCurrentPeriod(userId: String, id: String) {
            periodsStorage.values.filter { it.userId == userId }.forEach {
                periodsStorage[it.id] = it.copy(isCurrent = (it.id == id))
            }
        }

        override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> =
            flowOf(gradesStorage.values.filter { it.subjectId == subjectId })

        override suspend fun saveGrade(grade: Grade) {
            gradesStorage[grade.id] = grade
        }

        override suspend fun updateGrade(grade: Grade) {
            gradesStorage[grade.id] = grade
        }

        override suspend fun deleteGrade(id: String) {
            gradesStorage.remove(id)
        }

        override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> =
            flowOf(AcademicSummary(cumulativeGpa = 0.0, currentSemesterGpa = 0.0, earnedCredits = 0, targetCredits = 0, progressPercentage = 0.0))

        override suspend fun syncAcademicData(userId: String) {}
    }

    private lateinit var getAcademicPeriodsUseCase: GetAcademicPeriodsUseCase
    private lateinit var saveAcademicPeriodUseCase: SaveAcademicPeriodUseCase
    private lateinit var setCurrentPeriodUseCase: SetCurrentPeriodUseCase
    private lateinit var deleteAcademicPeriodUseCase: DeleteAcademicPeriodUseCase
    private lateinit var getGradesBySubjectUseCase: GetGradesBySubjectUseCase
    private lateinit var saveGradeUseCase: SaveGradeUseCase
    private lateinit var deleteGradeUseCase: DeleteGradeUseCase

    @Before
    fun setUp() {
        periodsStorage.clear()
        gradesStorage.clear()

        getAcademicPeriodsUseCase = GetAcademicPeriodsUseCase(fakeAcademicRepository)
        saveAcademicPeriodUseCase = SaveAcademicPeriodUseCase(fakeAcademicRepository)
        setCurrentPeriodUseCase = SetCurrentPeriodUseCase(fakeAcademicRepository)
        deleteAcademicPeriodUseCase = DeleteAcademicPeriodUseCase(fakeAcademicRepository)
        getGradesBySubjectUseCase = GetGradesBySubjectUseCase(fakeAcademicRepository)
        saveGradeUseCase = SaveGradeUseCase(fakeAcademicRepository)
        deleteGradeUseCase = DeleteGradeUseCase(fakeAcademicRepository)
    }

    @Test
    fun `saveAcademicPeriod stores period successfully`() = runBlocking {
        val period = AcademicPeriod(
            id = "p1",
            userId = "u1",
            studyId = "s1",
            name = "2026-1",
            startDate = "2026-02-01",
            endDate = "2026-06-30",
            isCurrent = false,
            createdAt = "2026-01-01",
            updatedAt = "2026-01-01"
        )

        saveAcademicPeriodUseCase(period)

        assertEquals(1, periodsStorage.size)
        assertEquals("2026-1", periodsStorage["p1"]?.name)
    }

    @Test
    fun `setCurrentPeriod marks target as current and others as not current`() = runBlocking {
        val p1 = AcademicPeriod("p1", "u1", "s1", "2025-2", "2025-08-01", "2025-12-15", true, "", "")
        val p2 = AcademicPeriod("p2", "u1", "s1", "2026-1", "2026-02-01", "2026-06-30", false, "", "")
        periodsStorage["p1"] = p1
        periodsStorage["p2"] = p2

        setCurrentPeriodUseCase("u1", "p2")

        assertEquals(false, periodsStorage["p1"]?.isCurrent)
        assertEquals(true, periodsStorage["p2"]?.isCurrent)
    }

    @Test
    fun `saveGrade stores grade and getGradesBySubject returns it`() = runBlocking {
        val grade = Grade(
            id = "g1",
            userId = "u1",
            subjectId = "sub1",
            academicPeriodId = "p1",
            name = "Examen Parcial 1",
            value = 4.2,
            weight = 0.3,
            notes = "Capítulos 1 a 4",
            createdAt = "2026-10-09",
            updatedAt = "2026-10-09"
        )

        saveGradeUseCase(grade)

        val retrieved = getGradesBySubjectUseCase("sub1").first()
        assertEquals(1, retrieved.size)
        assertEquals("Examen Parcial 1", retrieved[0].name)
        assertEquals(4.2, retrieved[0].value, 0.001)
    }

    @Test
    fun `deleteGrade removes grade from repository`() = runBlocking {
        val grade = Grade("g2", "u1", "sub1", "p1", "Quiz 1", 3.8, 0.1, null, "", "")
        gradesStorage["g2"] = grade

        deleteGradeUseCase("g2")

        assertNull(gradesStorage["g2"])
    }
}
