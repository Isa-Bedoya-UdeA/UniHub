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

fun Study.toEntity(): StudyEntity = StudyEntity(
    id = id,
    userId = userId,
    name = name,
    institution = institution,
    totalCredits = totalCredits,
    approvedCredits = approvedCredits,
    cumulativeGpa = cumulativeGpa,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun StudyEntity.toDomain(): Study = Study(
    id = id,
    userId = userId,
    name = name,
    institution = institution,
    totalCredits = totalCredits,
    approvedCredits = approvedCredits,
    cumulativeGpa = cumulativeGpa,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AcademicPeriod.toEntity(): AcademicPeriodEntity {
    return AcademicPeriodEntity(
        id = id,
        userId = userId,
        studyId = studyId,
        name = name,
        startDate = startDate,
        endDate = endDate,
        isCurrent = isCurrent,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun AcademicPeriodEntity.toDomain(): AcademicPeriod {
    return AcademicPeriod(
        id = id,
        userId = userId,
        studyId = studyId,
        name = name,
        startDate = startDate,
        endDate = endDate,
        isCurrent = isCurrent,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Grade.toEntity(): GradeEntity {
    return GradeEntity(
        id = id,
        userId = userId,
        subjectId = subjectId,
        academicPeriodId = academicPeriodId,
        name = name,
        value = value,
        weight = weight,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun GradeEntity.toDomain(): Grade {
    return Grade(
        id = id,
        userId = userId,
        subjectId = subjectId,
        academicPeriodId = academicPeriodId,
        name = name,
        value = value,
        weight = weight,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// DTO Mappers
fun Study.toDto(): StudyDto = StudyDto(
    id = id,
    userId = userId,
    name = name,
    institution = institution,
    totalCredits = totalCredits,
    approvedCredits = approvedCredits,
    cumulativeGpa = cumulativeGpa,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun StudyDto.toDomain(): Study = Study(
    id = id,
    userId = userId,
    name = name,
    institution = institution,
    totalCredits = totalCredits,
    approvedCredits = approvedCredits,
    cumulativeGpa = cumulativeGpa,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AcademicPeriod.toDto(): AcademicPeriodDto = AcademicPeriodDto(
    id = id,
    userId = userId,
    studyId = studyId,
    name = name,
    startDate = startDate,
    endDate = endDate,
    isCurrent = isCurrent,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AcademicPeriodDto.toDomain(): AcademicPeriod {
    return AcademicPeriod(
        id = id,
        userId = userId,
        studyId = studyId,
        name = name,
        startDate = startDate,
        endDate = endDate,
        isCurrent = isCurrent,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Grade.toDto(): GradeDto = GradeDto(
    id = id,
    userId = userId,
    subjectId = subjectId,
    academicPeriodId = academicPeriodId,
    name = name,
    value = value,
    weight = weight,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun GradeDto.toDomain(): Grade {
    return Grade(
        id = id,
        userId = userId,
        subjectId = subjectId,
        academicPeriodId = academicPeriodId,
        name = name,
        value = value,
        weight = weight,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
