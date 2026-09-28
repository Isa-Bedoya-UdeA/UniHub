package com.unihub.app.features.subjects.infrastructure.repository

import android.util.Log
import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import com.unihub.app.features.subjects.infrastructure.data.local.datasource.SubjectLocalDataSource
import com.unihub.app.features.subjects.infrastructure.data.mapper.toDomain
import com.unihub.app.features.subjects.infrastructure.data.mapper.toDto
import com.unihub.app.features.subjects.infrastructure.data.mapper.toEntity
import com.unihub.app.features.subjects.infrastructure.data.remote.datasource.SubjectRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImpl @Inject constructor(
    private val localDataSource: SubjectLocalDataSource,
    private val remoteDataSource: SubjectRemoteDataSource
) : SubjectRepository {

    override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> {
        return localDataSource.getSubjects(userId, academicPeriodId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllSubjects(userId: String): Flow<List<Subject>> {
        return localDataSource.getAllSubjects(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubjectById(id: String): Flow<Subject?> {
        return localDataSource.getSubjectById(id).map { it?.toDomain() }
    }

    override suspend fun saveSubject(subject: Subject) {
        Log.d("FIRESTORE_DEBUG", "=== SAVE SUBJECT CALLED ===")
        Log.d("FIRESTORE_DEBUG", "Subject ID: ${subject.id}")
        Log.d("FIRESTORE_DEBUG", "Subject Name: ${subject.name}")
        Log.d("FIRESTORE_DEBUG", "User ID: ${subject.userId}")
        
        localDataSource.insertSubject(subject.toEntity())
        Log.d("FIRESTORE_DEBUG", "Subject saved to Room successfully")
        
        try {
            Log.d("FIRESTORE_DEBUG", "About to call remoteDataSource.saveSubject()")
            val dto = subject.toDto()
            Log.d("FIRESTORE_DEBUG", "DTO created: $dto")
            remoteDataSource.saveSubject(dto)
            Log.d("FIRESTORE_DEBUG", "remoteDataSource.saveSubject() completed successfully")
        } catch (e: Exception) {
            Log.e("FIRESTORE_DEBUG", "ERROR saving subject to Firestore: ${e.message}", e)
            Log.e("FIRESTORE_DEBUG", "Exception type: ${e.javaClass.simpleName}")
            Log.e("FIRESTORE_DEBUG", "Stack trace: ${e.stackTraceToString()}")
        }
    }

    override suspend fun updateSubject(subject: Subject) {
        saveSubject(subject)
    }

    override suspend fun deleteSubject(id: String) {
        val subject = localDataSource.getSubjectById(id).firstOrNull()
        localDataSource.deleteSubject(id)
        if (subject != null) {
            try {
                remoteDataSource.deleteSubject(subject.userId, id)
            } catch (e: Exception) {
                Log.e("SubjectRepositoryImpl", "Error deleting subject from Firestore: ${e.message}")
            }
        }
    }

    override suspend fun syncSubjects(userId: String) {
        try {
            val remoteSubjects = remoteDataSource.getSubjects(userId)
            remoteSubjects.forEach { dto ->
                localDataSource.insertSubject(dto.toDomain().toEntity())
            }
        } catch (e: Exception) {
            Log.e("SubjectRepositoryImpl", "Error syncing subjects from Firestore: ${e.message}")
        }
    }
}
