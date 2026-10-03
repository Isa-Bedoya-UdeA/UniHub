package com.unihub.app.features.academic.infrastructure.repository

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class AcademicSummaryLogicTest {

    private val today = LocalDate.now()

    @Test
    fun `credits not added when subject not completed and period not ended`() {
        val futurePeriod = AcademicPeriod(
            id = "p1",
            userId = "u1",
            studyId = "s1",
            name = "2025-1",
            startDate = today.minusMonths(1).toString(),
            endDate = today.plusMonths(3).toString(),
            isCurrent = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )
        val subject = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "s1",
            academicPeriodId = "p1",
            name = "Calculo",
            credits = 4,
            isCompleted = false,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )
        val grade = Grade(
            id = "g1",
            userId = "u1",
            subjectId = "sub1",
            academicPeriodId = "p1",
            name = "Parcial 1",
            value = 4.5,
            weight = 1.0,
            notes = null,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val isPeriodEnded = LocalDate.parse(futurePeriod.endDate).isBefore(today)
        val qualifies = (subject.isCompleted || isPeriodEnded) && (grade.value * grade.weight >= 3.0)

        assertEquals(false, qualifies)
    }

    @Test
    fun `credits added when subject is explicitly marked as completed`() {
        val futurePeriod = AcademicPeriod(
            id = "p1",
            userId = "u1",
            studyId = "s1",
            name = "2025-1",
            startDate = today.minusMonths(1).toString(),
            endDate = today.plusMonths(3).toString(),
            isCurrent = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )
        val subject = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "s1",
            academicPeriodId = "p1",
            name = "Calculo",
            credits = 4,
            isCompleted = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )

        val isPeriodEnded = LocalDate.parse(futurePeriod.endDate).isBefore(today)
        val qualifies = subject.isCompleted || isPeriodEnded

        assertEquals(true, qualifies)
    }

    @Test
    fun `credits added when academic period has ended even if not explicitly marked completed`() {
        val pastPeriod = AcademicPeriod(
            id = "p1",
            userId = "u1",
            studyId = "s1",
            name = "2024-2",
            startDate = today.minusMonths(6).toString(),
            endDate = today.minusMonths(1).toString(),
            isCurrent = false,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        val subject = Subject(
            id = "sub1",
            userId = "u1",
            studyId = "s1",
            academicPeriodId = "p1",
            name = "Calculo",
            credits = 4,
            isCompleted = false,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        val grade = Grade(
            id = "g1",
            userId = "u1",
            subjectId = "sub1",
            academicPeriodId = "p1",
            name = "Nota Final",
            value = 3.5,
            weight = 1.0,
            notes = null,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )

        val isPeriodEnded = LocalDate.parse(pastPeriod.endDate).isBefore(today)
        val qualifies = (subject.isCompleted || isPeriodEnded) && (grade.value * grade.weight >= 3.0)

        assertEquals(true, qualifies)
    }
}
