package com.unihub.app.features.academic.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.academic.infrastructure.data.remote.dto.SubjectDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveSubject(subjectDto: SubjectDto) {
        try {
            val data = mapOf(
                "id" to subjectDto.id,
                "userId" to subjectDto.userId,
                "studyId" to subjectDto.studyId,
                "academicPeriodId" to subjectDto.academicPeriodId,
                "name" to subjectDto.name,
                "code" to subjectDto.code,
                "credits" to subjectDto.credits,
                "professor" to subjectDto.professor,
                "color" to subjectDto.color,
                "notes" to subjectDto.notes,
                "isCompleted" to subjectDto.isCompleted,
                "createdAt" to subjectDto.createdAt,
                "updatedAt" to subjectDto.updatedAt
            )
            
            Log.d("SubjectRemoteDataSource", "=== SAVING SUBJECT TO FIRESTORE ===")
            Log.d("SubjectRemoteDataSource", "Path: users/${subjectDto.userId}/subjects/${subjectDto.id}")
            
            firestore.collection("users")
                .document(subjectDto.userId)
                .collection("subjects")
                .document(subjectDto.id)
                .set(data)
                .await()
                
            Log.d("SubjectRemoteDataSource", "Subject saved successfully")
        } catch (e: Exception) {
            Log.e("SubjectRemoteDataSource", "Error saving subject: ${e.message}", e)
            throw e
        }
    }

    suspend fun getSubjects(userId: String): List<SubjectDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("subjects")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                SubjectDto(
                    id = data["id"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    studyId = data["studyId"] as? String ?: "",
                    academicPeriodId = data["academicPeriodId"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    code = data["code"] as? String,
                    credits = (data["credits"] as? Long)?.toInt(),
                    professor = data["professor"] as? String,
                    color = data["color"] as? String,
                    notes = data["notes"] as? String,
                    isCompleted = (data["isCompleted"] as? Boolean) ?: false,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("SubjectRemoteDataSource", "Error getting subjects: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteSubject(userId: String, subjectId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("subjects")
                .document(subjectId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("SubjectRemoteDataSource", "Error deleting subject: ${e.message}", e)
            throw e
        }
    }
}
