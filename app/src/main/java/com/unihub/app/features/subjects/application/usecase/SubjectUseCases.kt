package com.unihub.app.features.subjects.application.usecase

import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(private val repository: SubjectRepository) {
    operator fun invoke(userId: String, periodId: String) = repository.getSubjects(userId, periodId)
}

class GetAllSubjectsUseCase @Inject constructor(private val repository: SubjectRepository) {
    operator fun invoke(userId: String) = repository.getAllSubjects(userId)
}

class GetSubjectByIdUseCase @Inject constructor(private val repository: SubjectRepository) {
    operator fun invoke(id: String) = repository.getSubjectById(id)
}

class SaveSubjectUseCase @Inject constructor(private val repository: SubjectRepository) {
    suspend operator fun invoke(subject: Subject) = repository.saveSubject(subject)
}

class UpdateSubjectUseCase @Inject constructor(private val repository: SubjectRepository) {
    suspend operator fun invoke(subject: Subject) = repository.updateSubject(subject)
}

class DeleteSubjectUseCase @Inject constructor(private val repository: SubjectRepository) {
    suspend operator fun invoke(id: String) = repository.deleteSubject(id)
}
