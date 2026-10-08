package com.unihub.app.features.academic.infrastructure.repository

import android.util.Log
import com.unihub.app.features.academic.domain.model.Study
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.academic.infrastructure.data.local.datasource.StudyLocalDataSource
import com.unihub.app.features.academic.infrastructure.data.mapper.toDomain
import com.unihub.app.features.academic.infrastructure.data.mapper.toDto
import com.unihub.app.features.academic.infrastructure.data.mapper.toEntity
import com.unihub.app.features.academic.infrastructure.data.remote.datasource.StudyRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val localDataSource: StudyLocalDataSource,
    private val remoteDataSource: StudyRemoteDataSource
) : StudyRepository {

    override fun getStudies(userId: String): Flow<List<Study>> =
        localDataSource.getStudies(userId).map { entities -> entities.map { it.toDomain() } }

    override fun getStudyById(id: String): Flow<Study?> =
        localDataSource.getStudyById(id).map { it?.toDomain() }

    override suspend fun saveStudy(study: Study) {
        
        localDataSource.insertStudy(study.toEntity())
        
        try {
            val dto = study.toDto()
            remoteDataSource.saveStudy(dto)
        } catch (e: Exception) {
            Log.e("StudyRepositoryImpl", "Error saving study to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteStudy(id: String) {
        val study = localDataSource.getStudyById(id).firstOrNull()
        localDataSource.deleteStudy(id)
        if (study != null) {
            try {
                remoteDataSource.deleteStudy(study.userId, id)
            } catch (e: Exception) {
                Log.e("StudyRepositoryImpl", "Error deleting study from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun setActiveStudy(userId: String, studyId: String) {
        localDataSource.setActiveStudy(userId, studyId)
        try {
            val studyEntities = localDataSource.getStudies(userId).firstOrNull() ?: emptyList()
            studyEntities.forEach { entity ->
                val study = entity.toDomain()
                val updatedStudy = study.copy(isActive = study.id == studyId)
                remoteDataSource.saveStudy(updatedStudy.toDto())
            }
        } catch (e: Exception) {
            Log.e("StudyRepositoryImpl", "Error syncing active study to Firestore: ${e.message}")
        }
    }

    override suspend fun syncStudies(userId: String) {
        try {
            val remoteStudies = remoteDataSource.getStudies(userId)
            remoteStudies.forEach { dto ->
                localDataSource.insertStudy(dto.toDomain().toEntity())
            }
        } catch (e: Exception) {
            Log.e("StudyRepositoryImpl", "Error syncing studies from Firestore: ${e.message}")
        }
    }
}
