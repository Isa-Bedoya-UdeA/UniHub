package com.unihub.app.features.academic.application.usecase

import com.unihub.app.features.academic.domain.model.Subject
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SubjectUseCasesTest {

    private val subjectsStorage = mutableMapOf<String, Subject>()

    private val fakeSubjectRepository = object : SubjectRepository {
        override fun getSubjects(userId: String, periodId: String): Flow<List<Subject>> =
            flowOf(subjectsStorage.values.filter { it.userId == userId && it.academicPeriodId == periodId })

        override fun getAllSubjects(userId: String): Flow<List<Subject>> =
            flowOf(subjectsStorage.values.filter { it.userId == userId })

        override fun getSubjectById(id: String): Flow<Subject?> =
            flowOf(subjectsStorage[id])

        override suspend fun saveSubject(subject: Subject) {
            subjectsStorage[subject.id] = subject
        }

        override suspend fun updateSubject(subject: Subject) {
            subjectsStorage[subject.id] = subject
        }

        override suspend fun deleteSubject(id: String) {
            subjectsStorage.remove(id)
        }

        override suspend fun syncSubjects(userId: String) {}
    }

    private lateinit var getSubjectsUseCase: GetSubjectsUseCase
    private lateinit var getAllSubjectsUseCase: GetAllSubjectsUseCase
    private lateinit var getSubjectByIdUseCase: GetSubjectByIdUseCase
    private lateinit var saveSubjectUseCase: SaveSubjectUseCase
    private lateinit var updateSubjectUseCase: UpdateSubjectUseCase
    private lateinit var deleteSubjectUseCase: DeleteSubjectUseCase

    @Before
    fun setUp() {
        subjectsStorage.clear()
        getSubjectsUseCase = GetSubjectsUseCase(fakeSubjectRepository)
        getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepository)
        getSubjectByIdUseCase = GetSubjectByIdUseCase(fakeSubjectRepository)
        saveSubjectUseCase = SaveSubjectUseCase(fakeSubjectRepository)
        updateSubjectUseCase = UpdateSubjectUseCase(fakeSubjectRepository)
        deleteSubjectUseCase = DeleteSubjectUseCase(fakeSubjectRepository)
    }

    @Test
    fun `saveSubject stores subject correctly`() = runBlocking {
        val subject = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "study1",
            academicPeriodId = "p1",
            name = "Estructuras de Datos",
            code = "ED-101",
            credits = 3,
            professor = "Dr. Alan Turing",
            color = "#00FF00",
            notes = "Semestre 2",
            isCompleted = false,
            createdAt = "2026-10-09",
            updatedAt = "2026-10-09"
        )

        saveSubjectUseCase(subject)

        assertEquals(1, subjectsStorage.size)
        assertEquals("Estructuras de Datos", subjectsStorage["sub1"]?.name)
        assertEquals(3, subjectsStorage["sub1"]?.credits)
    }

    @Test
    fun `updateSubject modifies existing subject`() = runBlocking {
        val subject = Subject(
            id = "sub2",
            userId = "u1",
            studyId = "study1",
            academicPeriodId = "p1",
            name = "Redes 1",
            code = null,
            credits = 4,
            professor = null,
            color = null,
            notes = null,
            isCompleted = false,
            createdAt = "2026-10-09",
            updatedAt = "2026-10-09"
        )
        subjectsStorage["sub2"] = subject

        val updated = subject.copy(isCompleted = true, professor = "Ing. Vint Cerf")
        updateSubjectUseCase(updated)

        assertEquals(true, subjectsStorage["sub2"]?.isCompleted)
        assertEquals("Ing. Vint Cerf", subjectsStorage["sub2"]?.professor)
    }

    @Test
    fun `deleteSubject removes subject from storage`() = runBlocking {
        val subject = Subject(
            id = "sub3",
            userId = "u1",
            studyId = "study1",
            academicPeriodId = "p1",
            name = "Química",
            code = null,
            credits = 2,
            professor = null,
            color = null,
            notes = null,
            isCompleted = false,
            createdAt = "2026-10-09",
            updatedAt = "2026-10-09"
        )
        subjectsStorage["sub3"] = subject

        deleteSubjectUseCase("sub3")

        assertNull(subjectsStorage["sub3"])
    }
}
