package com.unihub.app.features.subjects.infrastructure.repository

import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImpl @Inject constructor() : SubjectRepository {

    private val mockSubjects = MutableStateFlow<List<Subject>>(
        listOf(
            Subject(
                id = "1", userId = "user123", academicPeriodId = "2026-2",
                name = "Computación Móvil", code = "COM-101", credits = 4,
                professor = "Elena Rodriguez", color = "#4F46E5",
                notes = "Laboratorio los jueves", createdAt = "", updatedAt = ""
            ),
            Subject(
                id = "2", userId = "user123", academicPeriodId = "2026-2",
                name = "Bases de Datos", code = "BD-303", credits = 4,
                professor = "Carlos Ruiz", color = "#7C3AED",
                notes = null, createdAt = "", updatedAt = ""
            ),
            Subject(
                id = "3", userId = "user123", academicPeriodId = "2026-2",
                name = "Cálculo Integral", code = "MAT-202", credits = 4,
                professor = "Dra. Maria Lopez", color = "#06B6D4",
                notes = null, createdAt = "", updatedAt = ""
            )
        )
    )

    override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> {
        return mockSubjects.map { list -> 
            list.filter { it.userId == userId && it.academicPeriodId == academicPeriodId } 
        }
    }

    override fun getSubjectById(id: String): Flow<Subject?> {
        return mockSubjects.map { list -> list.find { it.id == id } }
    }

    override suspend fun saveSubject(subject: Subject) {
        val current = mockSubjects.value.toMutableList()
        val index = current.indexOfFirst { it.id == subject.id }
        if (index != -1) {
            current[index] = subject
        } else {
            current.add(subject)
        }
        mockSubjects.emit(current)
    }

    override suspend fun updateSubject(subject: Subject) {
        saveSubject(subject)
    }

    override suspend fun deleteSubject(id: String) {
        val current = mockSubjects.value.filter { it.id != id }
        mockSubjects.emit(current)
    }

    override suspend fun syncSubjects(userId: String) {
        delay(500)
    }
}
