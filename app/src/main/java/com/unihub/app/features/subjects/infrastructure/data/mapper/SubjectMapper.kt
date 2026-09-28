package com.unihub.app.features.subjects.infrastructure.data.mapper

import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.infrastructure.data.local.entity.SubjectEntity
import com.unihub.app.features.subjects.infrastructure.data.remote.dto.SubjectDto

fun Subject.toEntity(): SubjectEntity {
    return SubjectEntity(
        id = id,
        userId = userId,
        studyId = studyId,
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
        studyId = studyId,
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

fun SubjectDto.toDomain(): Subject {
    return Subject(
        id = id,
        userId = userId,
        studyId = studyId,
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

fun Subject.toDto(): SubjectDto {
    return SubjectDto(
        id = id,
        userId = userId,
        studyId = studyId,
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
