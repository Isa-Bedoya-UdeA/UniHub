package com.unihub.app.features.academic.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.academic.application.usecase.SaveGradeUseCase
import com.unihub.app.features.academic.application.usecase.UpdateGradeUseCase
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.AcademicRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GradeFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val gradesStorage = mutableMapOf<String, Grade>()
    private val subjectsStorage = mutableMapOf<String, Subject>()

    private val fakeAuthRepository = object : AuthRepository {
        override fun observeAuthState(): Flow<AuthState> = flowOf(AuthState.Authenticated("u1"))
        override fun getCurrentUid(): String = "u1"
        override suspend fun signInWithGoogle(idToken: String): Result<String> = Result.success("u1")
        override suspend fun signOut() {}
        override suspend fun syncExistingUser(userId: String) {}
    }

    private val fakeSubjectRepository = object : SubjectRepository {
        override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> = flowOf(subjectsStorage.values.toList())
        override fun getAllSubjects(userId: String): Flow<List<Subject>> = flowOf(subjectsStorage.values.toList())
        override fun getSubjectById(id: String): Flow<Subject?> = flowOf(subjectsStorage[id])
        override suspend fun saveSubject(subject: Subject) {}
        override suspend fun updateSubject(subject: Subject) {}
        override suspend fun deleteSubject(id: String) {}
        override suspend fun syncSubjects(userId: String) {}
    }

    private val fakeAcademicRepository = object : AcademicRepository {
        override fun getAcademicPeriods(userId: String): Flow<List<AcademicPeriod>> = flowOf(emptyList())
        override fun getAcademicPeriodsByStudy(userId: String, studyId: String): Flow<List<AcademicPeriod>> = flowOf(emptyList())
        override suspend fun saveAcademicPeriod(period: AcademicPeriod) {}
        override suspend fun updateAcademicPeriod(period: AcademicPeriod) {}
        override suspend fun deleteAcademicPeriod(id: String) {}
        override suspend fun setCurrentPeriod(userId: String, id: String) {}
        override fun getGradesBySubject(subjectId: String): Flow<List<Grade>> = flowOf(gradesStorage.values.filter { it.subjectId == subjectId })
        override suspend fun saveGrade(grade: Grade) { gradesStorage[grade.id] = grade }
        override suspend fun updateGrade(grade: Grade) { gradesStorage[grade.id] = grade }
        override suspend fun deleteGrade(id: String) { gradesStorage.remove(id) }
        override fun getAcademicSummary(userId: String, studyId: String): Flow<AcademicSummary> =
            flowOf(AcademicSummary(cumulativeGpa = 0.0, currentSemesterGpa = 0.0, earnedCredits = 0, targetCredits = 0, progressPercentage = 0.0))
        override suspend fun syncAcademicData(userId: String) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        gradesStorage.clear()
        subjectsStorage.clear()

        subjectsStorage["sub1"] = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "study1",
            academicPeriodId = "p1",
            name = "Cálculo",
            code = null,
            credits = 4,
            professor = null,
            color = null,
            notes = null,
            isCompleted = false,
            createdAt = "",
            updatedAt = ""
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(subjectId: String = "sub1", gradeId: String? = null): GradeFormViewModel {
        val map = mutableMapOf<String, Any>("subjectId" to subjectId)
        if (gradeId != null) map["gradeId"] = gradeId
        val savedStateHandle = SavedStateHandle(map)

        return GradeFormViewModel(
            saveGradeUseCase = SaveGradeUseCase(fakeAcademicRepository),
            updateGradeUseCase = UpdateGradeUseCase(fakeAcademicRepository),
            getGradesBySubjectUseCase = GetGradesBySubjectUseCase(fakeAcademicRepository),
            getSubjectByIdUseCase = GetSubjectByIdUseCase(fakeSubjectRepository),
            getCurrentUidUseCase = GetCurrentUidUseCase(fakeAuthRepository),
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `initial state has empty fields and null errors`() {
        val viewModel = createViewModel()
        val state = viewModel.state.value

        assertEquals("", state.name)
        assertEquals("", state.value)
        assertEquals("", state.weight)
        assertNull(state.nameError)
        assertNull(state.valueError)
        assertNull(state.weightError)
    }

    @Test
    fun `save with blank name displays name error`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredValue("4.0"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("20"))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.nameError)
        assertEquals(0, gradesStorage.size)
    }

    @Test
    fun `save with grade value outside 0 to 5 displays value error`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Parcial 1"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("5.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("20"))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.valueError)
        assertEquals(0, gradesStorage.size)
    }

    @Test
    fun `save with valid inputs successfully stores grade`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Parcial 1"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("4.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("25"))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isSuccess)
        assertEquals(1, gradesStorage.size)
        val saved = gradesStorage.values.first()
        assertEquals("Parcial 1", saved.name)
        assertEquals(4.5, saved.value, 0.001)
        assertEquals(0.25, saved.weight, 0.001)
    }

    @Test
    fun `edit existing grade loads existing values into state`() = runTest {
        val existingGrade = Grade(
            id = "g1",
            userId = "u1",
            subjectId = "sub1",
            academicPeriodId = "p1",
            name = "Taller 1",
            value = 3.8,
            weight = 0.15,
            notes = "Nota individual",
            createdAt = "",
            updatedAt = ""
        )
        gradesStorage["g1"] = existingGrade

        val viewModel = createViewModel(subjectId = "sub1", gradeId = "g1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Taller 1", state.name)
        assertEquals("3.8", state.value)
        assertEquals("15", state.weight)
        assertEquals("Nota individual", state.notes)
    }
}
