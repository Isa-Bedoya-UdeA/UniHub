package com.unihub.app.features.academic.presentation.viewmodel

import com.unihub.app.features.academic.application.usecase.DeleteAcademicPeriodUseCase
import com.unihub.app.features.academic.application.usecase.DeleteSubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicPeriodsByStudyUseCase
import com.unihub.app.features.academic.application.usecase.GetAcademicSummaryUseCase
import com.unihub.app.features.academic.application.usecase.GetAllSubjectsUseCase
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetStudiesUseCase
import com.unihub.app.features.academic.application.usecase.GetSubjectsUseCase
import com.unihub.app.features.academic.application.usecase.SaveAcademicPeriodUseCase
import com.unihub.app.features.academic.application.usecase.SetActiveStudyUseCase
import com.unihub.app.features.academic.application.usecase.SetCurrentPeriodUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AcademicViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val studiesStorage = mutableListOf<Study>()
    private val periodsStorage = mutableMapOf<String, AcademicPeriod>()
    private val subjectsStorage = mutableMapOf<String, Subject>()
    private val gradesStorage = mutableMapOf<String, Grade>()

    private val fakeAuthRepository = object : AuthRepository {
        override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Authenticated("u1"))
        override fun getCurrentUid(): String = "u1"
        override suspend fun signInWithGoogle(idToken: String): Result<String> = Result.success("u1")
        override suspend fun signOut() {}
        override suspend fun syncExistingUser(userId: String) {}
    }

    private val fakeStudyRepository = object : StudyRepository {
        override fun getStudies(userId: String): Flow<List<Study>> = flowOf(studiesStorage)
        override fun getStudyById(id: String): Flow<Study?> = flowOf(studiesStorage.find { it.id == id })
        override suspend fun saveStudy(study: Study) { studiesStorage.add(study) }
        override suspend fun deleteStudy(id: String) { studiesStorage.removeAll { it.id == id } }
        override suspend fun setActiveStudy(userId: String, studyId: String) {
            val list = studiesStorage.map { it.copy(isActive = (it.id == studyId)) }
            studiesStorage.clear()
            studiesStorage.addAll(list)
        }
        override suspend fun syncStudies(userId: String) {}
    }

    private val fakeAcademicRepository = object : AcademicRepository {
        override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> = flowOf(periodsStorage.values.toList())
        override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> =
            flowOf(periodsStorage.values.filter { it.studyId == studyId })
        override suspend fun saveAcademicPeriod(period: AcademicPeriod) { periodsStorage[period.id] = period }
        override suspend fun updateAcademicPeriod(period: AcademicPeriod) { periodsStorage[period.id] = period }
        override suspend fun deleteAcademicPeriod(id: String) { periodsStorage.remove(id) }
        override suspend fun setCurrentPeriod(userId: String, id: String) {}
        override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> = flowOf(gradesStorage.values.filter { it.subjectId == subjectId })
        override suspend fun saveGrade(grade: Grade) { gradesStorage[grade.id] = grade }
        override suspend fun updateGrade(grade: Grade) { gradesStorage[grade.id] = grade }
        override suspend fun deleteGrade(id: String) { gradesStorage.remove(id) }
        override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> =
            flowOf(AcademicSummary(cumulativeGpa = 4.2, currentSemesterGpa = 4.0, earnedCredits = 20, targetCredits = 100, progressPercentage = 20.0))
        override suspend fun syncAcademicData(userId: String) {}
    }

    private val fakeSubjectRepository = object : SubjectRepository {
        override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> =
            flowOf(subjectsStorage.values.filter { it.academicPeriodId == academicPeriodId })
        override fun getAllSubjects(userId: String): Flow<List<Subject>> = flowOf(subjectsStorage.values.toList())
        override fun getSubjectById(id: String): Flow<Subject?> = flowOf(subjectsStorage[id])
        override suspend fun saveSubject(subject: Subject) { subjectsStorage[subject.id] = subject }
        override suspend fun updateSubject(subject: Subject) { subjectsStorage[subject.id] = subject }
        override suspend fun deleteSubject(id: String) { subjectsStorage.remove(id) }
        override suspend fun syncSubjects(userId: String) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        studiesStorage.clear()
        periodsStorage.clear()
        subjectsStorage.clear()
        gradesStorage.clear()

        studiesStorage.add(Study("st1", "u1", "Ingeniería de Sistemas", "UdeA", 160, 45, 4.1, true, "", ""))
        periodsStorage["p1"] = AcademicPeriod("p1", "u1", "st1", "2026-1", "2026-02-01", "2026-06-30", true, "", "")
        subjectsStorage["sub1"] = Subject("sub1", "u1", "st1", "p1", "Bases de Datos", null, 3, null, null, null, false, "", "")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AcademicViewModel {
        return AcademicViewModel(
            getStudiesUseCase = GetStudiesUseCase(fakeStudyRepository),
            getAcademicSummaryUseCase = GetAcademicSummaryUseCase(fakeAcademicRepository),
            getSubjectsUseCase = GetSubjectsUseCase(fakeSubjectRepository),
            getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepository),
            getAcademicPeriodsByStudyUseCase = GetAcademicPeriodsByStudyUseCase(fakeAcademicRepository),
            setActiveStudyUseCase = SetActiveStudyUseCase(fakeStudyRepository),
            setCurrentPeriodUseCase = SetCurrentPeriodUseCase(fakeAcademicRepository),
            saveAcademicPeriodUseCase = SaveAcademicPeriodUseCase(fakeAcademicRepository),
            deleteAcademicPeriodUseCase = DeleteAcademicPeriodUseCase(fakeAcademicRepository),
            deleteSubjectUseCase = DeleteSubjectUseCase(fakeSubjectRepository),
            getGradesBySubjectUseCase = GetGradesBySubjectUseCase(fakeAcademicRepository),
            getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepository)
        )
    }

    @Test
    fun `initialization loads studies, active study, and academic periods`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("st1", state.selectedStudyId)
        assertEquals(1, state.studies.size)
        assertEquals("p1", state.selectedPeriodId)
    }

    @Test
    fun `deleteAcademicPeriod removes period from storage`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.deletePeriod("p1")
        advanceUntilIdle()

        assertEquals(0, periodsStorage.size)
    }

    @Test
    fun `deleteSubject removes subject from storage`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.deleteSubject("sub1")
        advanceUntilIdle()

        assertEquals(0, subjectsStorage.size)
    }
}
