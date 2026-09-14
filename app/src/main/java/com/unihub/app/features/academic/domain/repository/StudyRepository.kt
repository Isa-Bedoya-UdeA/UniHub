package com.unihub.app.features.academic.domain.repository

import com.unihub.app.features.academic.domain.model.Study
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    fun getStudies(userId: String): Flow<List<Study>>
    fun getStudyById(id: String): Flow<Study?>
    suspend fun saveStudy(study: Study)
    suspend fun deleteStudy(id: String)
    suspend fun setActiveStudy(userId: String, studyId: String)
}
