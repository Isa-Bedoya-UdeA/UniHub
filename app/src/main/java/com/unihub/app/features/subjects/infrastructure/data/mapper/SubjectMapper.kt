package com.unihub.app.features.subjects.infrastructure.data.mapper

import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.infrastructure.data.local.entity.SubjectEntity
import com.unihub.app.features.subjects.infrastructure.data.remote.dto.SubjectDto

fun Subject.toEntity(): SubjectEntity {
    return SubjectEntity(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        name = name,
        code = code,
        credits = credits,
        professor = professor,
        color = color,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun SubjectEntity.toDomain(): Subject {
    return Subject(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        name = name,
        code = code,
        credits = credits,
        professor = professor,
        color = color,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// Note: DTO conversion might need logic to handle userId or date string/long conversions
fun SubjectDto.toDomain(userId: String): Subject {
    return Subject(
        id = id,
        userId = userId,
        academicPeriodId = academicPeriodId,
        name = name,
        code = code,
        credits = credits,
        professor = professor,
        color = color,
        notes = notes,
        createdAt = createdAt.toString(), // Simplified
        updatedAt = updatedAt.toString()
    )
}

fun Subject.toDto(): SubjectDto {
    return SubjectDto(
        id = id,
        academicPeriodId = academicPeriodId,
        name = name,
        code = code,
        credits = credits,
        professor = professor,
        color = color,
        notes = notes,
        createdAt = createdAt.toLongOrNull() ?: 0L,
        updatedAt = updatedAt.toLongOrNull() ?: 0L
    )
}
