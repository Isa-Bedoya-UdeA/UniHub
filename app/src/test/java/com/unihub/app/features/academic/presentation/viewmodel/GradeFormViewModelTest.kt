package com.unihub.app.features.academic.presentation.viewmodel

import com.unihub.app.features.academic.application.usecase.GetGradesBySubjectUseCase
import com.unihub.app.features.academic.application.usecase.GetSubjectByIdUseCase
import com.unihub.app.features.academic.application.usecase.SaveGradeUseCase
import com.unihub.app.features.academic.application.usecase.UpdateGradeUseCase
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.auth.application.usecase.GetCurrentUidUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GradeFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GradeFormViewModel
    
    // Fake implementations
    private val fakeSaveGradeUseCase = object : SaveGradeUseCase {
        override suspend fun invoke(grade: Grade) {}
    }
    
    private val fakeUpdateGradeUseCase = object : UpdateGradeUseCase {
        override suspend fun invoke(grade: Grade) {}
    }
    
    private val fakeGetGradesBySubjectUseCase = object : GetGradesBySubjectUseCase {
        override fun invoke(subjectId: String) = flowOf(emptyList<Grade>())
    }
    
    private val fakeGetSubjectByIdUseCase = object : GetSubjectByIdUseCase {
        override fun invoke(subjectId: String) = flowOf<Subject?>(null)
    }
    
    private val fakeGetCurrentUidUseCase = object : GetCurrentUidUseCase {
        override fun invoke() = "test-user-id"
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(subjectId: String = "subject-1", gradeId: String? = null): GradeFormViewModel {
        val savedStateHandle = androidx.lifecycle.SavedStateHandle().apply {
            set("subjectId", subjectId)
            gradeId?.let { set("gradeId", it) }
        }
        return GradeFormViewModel(
            fakeSaveGradeUseCase,
            fakeUpdateGradeUseCase,
            fakeGetGradesBySubjectUseCase,
            fakeGetSubjectByIdUseCase,
            fakeGetCurrentUidUseCase,
            savedStateHandle
        )
    }

    @Test
    fun `validateInputs rejects blank name`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName(""))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("El nombre es obligatorio", viewModel.state.value.nameError)
    }

    @Test
    fun `validateInputs rejects name longer than 100 characters`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("A".repeat(101)))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("El nombre no puede exceder 100 caracteres", viewModel.state.value.nameError)
    }

    @Test
    fun `validateInputs rejects non-numeric grade value`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("abc"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("La nota debe ser un número válido", viewModel.state.value.valueError)
    }

    @Test
    fun `validateInputs rejects grade value out of range`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("6.0"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("La nota debe estar entre 0.0 y 5.0", viewModel.state.value.valueError)
    }

    @Test
    fun `validateInputs rejects negative grade value`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("-1.0"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("La nota debe estar entre 0.0 y 5.0", viewModel.state.value.valueError)
    }

    @Test
    fun `validateInputs rejects non-numeric weight`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("abc"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("El peso debe ser un número válido", viewModel.state.value.weightError)
    }

    @Test
    fun `validateInputs rejects weight out of range`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("150"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("El peso debe estar entre 1 y 100", viewModel.state.value.weightError)
    }

    @Test
    fun `validateInputs rejects zero weight`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("0"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("El peso debe estar entre 1 y 100", viewModel.state.value.weightError)
    }

    @Test
    fun `validateInputs rejects notes longer than 500 characters`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))
        viewModel.onEvent(GradeFormEvent.EnteredNotes("A".repeat(501)))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertEquals("Las notas no pueden exceder 500 caracteres", viewModel.state.value.errorMessage)
    }

    @Test
    fun `validateInputs accepts valid inputs`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName("Midterm"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("50"))
        viewModel.onEvent(GradeFormEvent.EnteredNotes("Good performance"))

        viewModel.onEvent(GradeFormEvent.SaveGrade)

        assertNull(viewModel.state.value.nameError)
        assertNull(viewModel.state.value.valueError)
        assertNull(viewModel.state.value.weightError)
    }

    @Test
    fun `validateInputs accepts boundary values`() {
        viewModel = createViewModel()
        
        // Test minimum grade
        viewModel.onEvent(GradeFormEvent.EnteredName("Min"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("0.0"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("1"))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        assertNull(viewModel.state.value.valueError)
        assertNull(viewModel.state.value.weightError)

        // Test maximum grade
        viewModel.onEvent(GradeFormEvent.EnteredName("Max"))
        viewModel.onEvent(GradeFormEvent.EnteredValue("5.0"))
        viewModel.onEvent(GradeFormEvent.EnteredWeight("100"))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        assertNull(viewModel.state.value.valueError)
        assertNull(viewModel.state.value.weightError)
    }

    @Test
    fun `clearError event clears error message`() {
        viewModel = createViewModel()
        viewModel.onEvent(GradeFormEvent.EnteredName(""))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        
        assertTrue(viewModel.state.value.nameError != null)
        
        viewModel.onEvent(GradeFormEvent.ClearError)
        
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `entering new value clears previous error`() {
        viewModel = createViewModel()
        
        // Trigger error
        viewModel.onEvent(GradeFormEvent.EnteredValue("abc"))
        viewModel.onEvent(GradeFormEvent.SaveGrade)
        assertTrue(viewModel.state.value.valueError != null)
        
        // Enter valid value
        viewModel.onEvent(GradeFormEvent.EnteredValue("3.5"))
        
        // Error should be cleared
        assertNull(viewModel.state.value.valueError)
    }
}
