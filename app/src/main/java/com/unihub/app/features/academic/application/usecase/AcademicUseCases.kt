package com.unihub.app.features.academic.application.usecase

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import javax.inject.Inject

class GetAcademicPeriodsUseCase @Inject constructor(private val repository: AcademicRepository) {
    operator fun invoke(userId: String) = repository.getAcademicPeriods(userId)
}

class GetAcademicPeriodsByStudyUseCase @Inject constructor(private val repository: AcademicRepository) {
    operator fun invoke(userId: String, studyId: String) = repository.getAcademicPeriodsByStudy(userId, studyId)
}

class SaveAcademicPeriodUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(period: AcademicPeriod) = repository.saveAcademicPeriod(period)
}

class UpdateAcademicPeriodUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(period: AcademicPeriod) = repository.updateAcademicPeriod(period)
}

class DeleteAcademicPeriodUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(id: String) = repository.deleteAcademicPeriod(id)
}

class SetCurrentPeriodUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(userId: String, id: String) = repository.setCurrentPeriod(userId, id)
}

class GetGradesBySubjectUseCase @Inject constructor(private val repository: AcademicRepository) {
    operator fun invoke(subjectId: String) = repository.getGradesBySubject(subjectId)
}

class SaveGradeUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(grade: Grade) = repository.saveGrade(grade)
}

class UpdateGradeUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(grade: Grade) = repository.updateGrade(grade)
}

class DeleteGradeUseCase @Inject constructor(private val repository: AcademicRepository) {
    suspend operator fun invoke(id: String) = repository.deleteGrade(id)
}

class GetAcademicSummaryUseCase @Inject constructor(private val repository: AcademicRepository) {
    operator fun invoke(userId: String, studyId: String) = repository.getAcademicSummary(userId, studyId)
}
