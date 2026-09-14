package com.unihub.app.features.academic.application.usecase

import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.repository.StudyRepository
import javax.inject.Inject

class GetStudiesUseCase @Inject constructor(private val repository: StudyRepository) {
    operator fun invoke(userId: String) = repository.getStudies(userId)
}

class SaveStudyUseCase @Inject constructor(private val repository: StudyRepository) {
    suspend operator fun invoke(study: Study) = repository.saveStudy(study)
}

class DeleteStudyUseCase @Inject constructor(private val repository: StudyRepository) {
    suspend operator fun invoke(id: String) = repository.deleteStudy(id)
}

class SetActiveStudyUseCase @Inject constructor(private val repository: StudyRepository) {
    suspend operator fun invoke(userId: String, studyId: String) = repository.setActiveStudy(userId, studyId)
}
