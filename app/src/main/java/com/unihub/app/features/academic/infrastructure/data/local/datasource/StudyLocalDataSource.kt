package com.unihub.app.features.academic.infrastructure.data.local.datasource

import com.unihub.app.features.academic.infrastructure.data.local.dao.StudyDao
import com.unihub.app.features.academic.infrastructure.data.local.entity.StudyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyLocalDataSource @Inject constructor(
    private val studyDao: StudyDao
) {
    fun getStudies(userId: String): Flow<List<StudyEntity>> = studyDao.getStudies(userId)

    fun getStudyById(id: String): Flow<StudyEntity?> = studyDao.getStudyById(id)

    suspend fun insertStudy(study: StudyEntity) = studyDao.insertStudy(study)

    suspend fun deleteStudy(id: String) = studyDao.deleteStudy(id)

    suspend fun setActiveStudy(userId: String, studyId: String) = studyDao.setActiveStudy(userId, studyId)
}
