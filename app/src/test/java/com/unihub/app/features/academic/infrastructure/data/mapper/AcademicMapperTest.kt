package com.unihub.app.features.academic.infrastructure.data.mapper

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.infrastructure.data.local.entity.AcademicPeriodEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.GradeEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.StudyEntity
import com.unihub.app.features.academic.infrastructure.data.remote.dto.AcademicPeriodDto
import com.unihub.app.features.academic.infrastructure.data.remote.dto.GradeDto
import com.unihub.app.features.academic.infrastructure.data.remote.dto.StudyDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AcademicMapperTest {

    @Test
    fun `Study toEntity and back preserves data`() {
        val study = Study(
            id = "s1",
            userId = "u1",
            name = "Ing Sistemas",
            institution = "UdeA",
            totalCredits = 160,
            approvedCredits = 80,
            cumulativeGpa = 4.2,
            isActive = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-06-01"
        )

        val entity = study.toEntity()
        val restored = entity.toDomain()

        assertEquals(study, restored)
    }

    @Test
    fun `Study toDto and back preserves data`() {
        val study = Study(
            id = "s1",
            userId = "u1",
            name = "Ing Sistemas",
            institution = "UdeA",
            totalCredits = 160,
            approvedCredits = null,
            cumulativeGpa = null,
            isActive = false,
            createdAt = "2025-01-01",
            updatedAt = "2025-06-01"
        )

        val dto = study.toDto()
        val restored = dto.toDomain()

        assertEquals(study, restored)
    }

    @Test
    fun `AcademicPeriod toEntity and back preserves data`() {
        val period = AcademicPeriod(
            id = "p1",
            userId = "u1",
            studyId = "s1",
            name = "2025-1",
            startDate = "2025-02-01",
            endDate = "2025-06-30",
            isCurrent = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val entity = period.toEntity()
        val restored = entity.toDomain()

        assertEquals(period, restored)
    }

    @Test
    fun `Grade toEntity and back preserves data`() {
        val grade = Grade(
            id = "g1",
            userId = "u1",
            subjectId = "sub1",
            academicPeriodId = "p1",
            name = "Parcial 1",
            value = 4.5,
            weight = 0.3,
            notes = "Buen trabajo",
            createdAt = "2025-03-01",
            updatedAt = "2025-03-01"
        )

        val entity = grade.toEntity()
        val restored = entity.toDomain()

        assertEquals(grade, restored)
    }

    @Test
    fun `Grade toDto and back preserves data`() {
        val grade = Grade(
            id = "g1",
            userId = "u1",
            subjectId = "sub1",
            academicPeriodId = "p1",
            name = "Parcial 1",
            value = 3.8,
            weight = 0.25,
            notes = null,
            createdAt = "2025-03-01",
            updatedAt = "2025-03-01"
        )

        val dto = grade.toDto()
        val restored = dto.toDomain()

        assertEquals(grade, restored)
    }

    @Test
    fun `Study with null optional fields maps correctly`() {
        val study = Study(
            id = "s1",
            userId = "u1",
            name = "Test",
            institution = "Test",
            totalCredits = 100,
            approvedCredits = null,
            cumulativeGpa = null,
            isActive = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val dto = study.toDto()
        assertNull(dto.approvedCredits)
        assertNull(dto.cumulativeGpa)

        val restored = dto.toDomain()
        assertNull(restored.approvedCredits)
        assertNull(restored.cumulativeGpa)
    }
}
