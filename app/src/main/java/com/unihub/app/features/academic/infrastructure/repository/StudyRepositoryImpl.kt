package com.unihub.app.features.academic.infrastructure.repository

import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.academic.infrastructure.data.local.dao.StudyDao
import com.unihub.app.features.academic.infrastructure.data.mapper.toDomain
import com.unihub.app.features.academic.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val studyDao: StudyDao
) : StudyRepository {
    override fun getStudies(userId: String): Flow<List<Study>> =
        studyDao.getStudies(userId).map { entities -> entities.map { it.toDomain() } }

    override fun getStudyById(id: String): Flow<Study?> =
        studyDao.getStudyById(id).map { it?.toDomain() }

    override suspend fun saveStudy(study: Study) {
        studyDao.insertStudy(study.toEntity())
    }

    override suspend fun deleteStudy(id: String) {
        studyDao.deleteStudy(id)
    }

    override suspend fun setActiveStudy(userId: String, studyId: String) {
        studyDao.setActiveStudy(userId, studyId)
    }
}
