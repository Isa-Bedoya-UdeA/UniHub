package com.unihub.app.features.academic.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.academic.infrastructure.data.remote.dto.AcademicPeriodDto
import com.unihub.app.features.academic.infrastructure.data.remote.dto.GradeDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademicRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveAcademicPeriod(periodDto: AcademicPeriodDto) {
        try {
            val data = mapOf(
                "id" to periodDto.id,
                "userId" to periodDto.userId,
                "studyId" to periodDto.studyId,
                "name" to periodDto.name,
                "startDate" to periodDto.startDate,
                "endDate" to periodDto.endDate,
                "isCurrent" to periodDto.isCurrent,
                "createdAt" to periodDto.createdAt,
                "updatedAt" to periodDto.updatedAt
            )
            
            Log.d("AcademicRemoteDataSource", "=== SAVING ACADEMIC PERIOD TO FIRESTORE ===")
            Log.d("AcademicRemoteDataSource", "Path: users/${periodDto.userId}/academicPeriods/${periodDto.id}")
            
            firestore.collection("users")
                .document(periodDto.userId)
                .collection("academicPeriods")
                .document(periodDto.id)
                .set(data)
                .await()
                
            Log.d("AcademicRemoteDataSource", "Academic period saved successfully")
        } catch (e: Exception) {
            Log.e("AcademicRemoteDataSource", "Error saving academic period: ${e.message}", e)
            throw e
        }
    }

    suspend fun getAcademicPeriods(userId: String): List<AcademicPeriodDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("academicPeriods")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                AcademicPeriodDto(
                    id = data["id"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    studyId = data["studyId"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    startDate = data["startDate"] as? String ?: "",
                    endDate = data["endDate"] as? String ?: "",
                    isCurrent = data["isCurrent"] as? Boolean ?: false,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("AcademicRemoteDataSource", "Error getting academic periods: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteAcademicPeriod(userId: String, periodId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("academicPeriods")
                .document(periodId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("AcademicRemoteDataSource", "Error deleting academic period: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveGrade(gradeDto: GradeDto) {
        try {
            val data = mapOf(
                "id" to gradeDto.id,
                "userId" to gradeDto.userId,
                "subjectId" to gradeDto.subjectId,
                "academicPeriodId" to gradeDto.academicPeriodId,
                "name" to gradeDto.name,
                "value" to gradeDto.value,
                "weight" to gradeDto.weight,
                "notes" to gradeDto.notes,
                "createdAt" to gradeDto.createdAt,
                "updatedAt" to gradeDto.updatedAt
            )
            
            Log.d("AcademicRemoteDataSource", "=== SAVING GRADE TO FIRESTORE ===")
            Log.d("AcademicRemoteDataSource", "Path: users/${gradeDto.userId}/grades/${gradeDto.id}")
            
            firestore.collection("users")
                .document(gradeDto.userId)
                .collection("grades")
                .document(gradeDto.id)
                .set(data)
                .await()
                
            Log.d("AcademicRemoteDataSource", "Grade saved successfully")
        } catch (e: Exception) {
            Log.e("AcademicRemoteDataSource", "Error saving grade: ${e.message}", e)
            throw e
        }
    }

    suspend fun getGrades(userId: String): List<GradeDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("grades")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                GradeDto(
                    id = data["id"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    subjectId = data["subjectId"] as? String ?: "",
                    academicPeriodId = data["academicPeriodId"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    value = data["value"] as? Double ?: 0.0,
                    weight = data["weight"] as? Double ?: 0.0,
                    notes = data["notes"] as? String,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("AcademicRemoteDataSource", "Error getting grades: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteGrade(userId: String, gradeId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("grades")
                .document(gradeId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("AcademicRemoteDataSource", "Error deleting grade: ${e.message}", e)
            throw e
        }
    }
}
