package com.unihub.app.features.academic.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.academic.infrastructure.data.remote.dto.StudyDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveStudy(studyDto: StudyDto) {
        try {
            val data = mapOf(
                "id" to studyDto.id,
                "userId" to studyDto.userId,
                "name" to studyDto.name,
                "institution" to studyDto.institution,
                "totalCredits" to studyDto.totalCredits,
                "approvedCredits" to studyDto.approvedCredits,
                "cumulativeGpa" to studyDto.cumulativeGpa,
                "isActive" to studyDto.isActive,
                "createdAt" to studyDto.createdAt,
                "updatedAt" to studyDto.updatedAt
            )
            
            Log.d("StudyRemoteDataSource", "=== SAVING STUDY TO FIRESTORE ===")
            Log.d("StudyRemoteDataSource", "Path: users/${studyDto.userId}/studies/${studyDto.id}")
            Log.d("StudyRemoteDataSource", "Data: $data")
            
            firestore.collection("users")
                .document(studyDto.userId)
                .collection("studies")
                .document(studyDto.id)
                .set(data)
                .await()
                
            Log.d("StudyRemoteDataSource", "Study saved successfully")
        } catch (e: Exception) {
            Log.e("StudyRemoteDataSource", "Error saving study: ${e.message}", e)
            throw e
        }
    }

    suspend fun getStudies(userId: String): List<StudyDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("studies")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                StudyDto(
                    id = data["id"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    institution = data["institution"] as? String ?: "",
                    totalCredits = (data["totalCredits"] as? Long)?.toInt() ?: 0,
                    approvedCredits = (data["approvedCredits"] as? Long)?.toInt(),
                    cumulativeGpa = data["cumulativeGpa"] as? Double,
                    isActive = data["isActive"] as? Boolean ?: false,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("StudyRemoteDataSource", "Error getting studies: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteStudy(userId: String, studyId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("studies")
                .document(studyId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("StudyRemoteDataSource", "Error deleting study: ${e.message}", e)
            throw e
        }
    }
}
