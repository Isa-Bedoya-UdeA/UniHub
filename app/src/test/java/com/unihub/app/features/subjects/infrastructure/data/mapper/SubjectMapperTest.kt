package com.unihub.app.features.subjects.infrastructure.data.mapper

import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.infrastructure.data.remote.dto.SubjectDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SubjectMapperTest {

    @Test
    fun `Subject toEntity and back preserves data`() {
        val subject = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "s1",
            academicPeriodId = "p1",
            name = "Calculo I",
            code = "MAT101",
            credits = 4,
            professor = "Dr. Garcia",
            color = "#FF0000",
            notes = "Llevar calculadora",
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val entity = subject.toEntity()
        val restored = entity.toDomain()

        assertEquals(subject, restored)
    }

    @Test
    fun `Subject toDto and back preserves data`() {
        val subject = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "s1",
            academicPeriodId = "p1",
            name = "Calculo I",
            code = null,
            credits = null,
            professor = null,
            color = null,
            notes = null,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val dto = subject.toDto()
        val restored = dto.toDomain()

        assertEquals(subject, restored)
    }

    @Test
    fun `SubjectDto with all null optional fields maps correctly`() {
        val dto = SubjectDto(
            id = "sub1",
            userId = "u1",
            studyId = "s1",
            academicPeriodId = "p1",
            name = "Test",
            code = null,
            credits = null,
            professor = null,
            color = null,
            notes = null,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val subject = dto.toDomain()

        assertNull(subject.code)
        assertNull(subject.credits)
        assertNull(subject.professor)
        assertNull(subject.color)
        assertNull(subject.notes)
    }
}
