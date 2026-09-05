package com.unihub.app.features.academic.infrastructure.data.mapper

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.infrastructure.data.local.entity.AcademicPeriodEntity
import com.unihub.app.features.academic.infrastructure.data.local.entity.GradeEntity
import com.unihub.app.features.academic.infrastructure.data.remote.dto.AcademicPeriodDto
import com.unihub.app.features.academic.infrastructure.data.remote.dto.GradeDto

fun AcademicPeriod.toEntity(): AcademicPeriodEntity {
    return AcademicPeriodEntity(
        id = id,
        userId = userId,
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

// DTO Mappers (Simplifying timestamp conversions)
fun AcademicPeriodDto.toDomain(userId: String): AcademicPeriod {
    return AcademicPeriod(
        id = id,
        userId = userId,
        name = name,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        isCurrent = isCurrent,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

fun GradeDto.toDomain(userId: String): Grade {
    return Grade(
        id = id,
        userId = userId,
        subjectId = subjectId,
        academicPeriodId = academicPeriodId,
        name = name,
        value = value,
        weight = weight,
        notes = notes,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}
