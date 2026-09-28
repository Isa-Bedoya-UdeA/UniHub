package com.unihub.app.features.tasks.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.tasks.infrastructure.data.remote.dto.TagDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveTag(userId: String, tagDto: TagDto) {
        try {
            val data = mapOf(
                "id" to tagDto.id,
                "userId" to tagDto.userId,
                "name" to tagDto.name,
                "createdAt" to tagDto.createdAt
            )
            
            Log.d("TagRemoteDataSource", "=== SAVING TAG TO FIRESTORE ===")
            Log.d("TagRemoteDataSource", "Path: users/$userId/tags/${tagDto.id}")
            
            firestore.collection("users")
                .document(userId)
                .collection("tags")
                .document(tagDto.id)
                .set(data)
                .await()
                
            Log.d("TagRemoteDataSource", "Tag saved successfully")
        } catch (e: Exception) {
            Log.e("TagRemoteDataSource", "Error saving tag: ${e.message}", e)
            throw e
        }
    }

    suspend fun getTags(userId: String): List<TagDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("tags")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                TagDto(
                    id = data["id"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    name = data["name"] as? String ?: "",
                    createdAt = data["createdAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("TagRemoteDataSource", "Error getting tags: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteTag(userId: String, tagId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("tags")
                .document(tagId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("TagRemoteDataSource", "Error deleting tag: ${e.message}", e)
            throw e
        }
    }
}
