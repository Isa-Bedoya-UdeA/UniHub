package com.unihub.app.features.auth.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.auth.infrastructure.data.remote.dto.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class ProfileFetchResult {
    data class Found(val user: UserDto) : ProfileFetchResult()
    data object NotFound : ProfileFetchResult()
    data class FetchError(val exception: Exception) : ProfileFetchResult()
}

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveUser(userDto: UserDto) {
        val data = mapOf(
            "userId" to userDto.userId,
            "name" to userDto.name,
            "email" to userDto.email,
            "profileImageUrl" to userDto.profileImageUrl,
            "createdAt" to userDto.createdAt,
            "updatedAt" to userDto.updatedAt
        )
        firestore.collection("users")
            .document(userDto.userId)
            .collection("profile")
            .document("main")
            .set(data)
            .await()
    }

    suspend fun getUser(userId: String): ProfileFetchResult {
        return try {
            Log.d("UserRemoteDataSource", "=== FETCHING PROFILE FROM FIRESTORE ===")
            Log.d("UserRemoteDataSource", "Path: users/$userId/profile/main")
            
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("profile")
                .document("main")
                .get()
                .await()

            Log.d("UserRemoteDataSource", "Document exists: ${snapshot.exists()}")
            Log.d("UserRemoteDataSource", "Document data: ${snapshot.data}")

            if (!snapshot.exists()) {
                Log.d("UserRemoteDataSource", "Profile document does not exist for user: $userId")
                ProfileFetchResult.NotFound
            } else {
                val data = snapshot.data
                if (data == null) {
                    Log.w("UserRemoteDataSource", "Profile document exists but data is null for user: $userId")
                    ProfileFetchResult.NotFound
                } else {
                    val profileImageUrl = data["profileImageUrl"] as? String
                    Log.d("UserRemoteDataSource", "=== PROFILE FOUND ===")
                    Log.d("UserRemoteDataSource", "Extracted profileImageUrl: $profileImageUrl")
                    Log.d("UserRemoteDataSource", "Extracted userId: ${data["userId"]}")
                    Log.d("UserRemoteDataSource", "Extracted name: ${data["name"]}")
                    
                    ProfileFetchResult.Found(
                        UserDto(
                            userId = data["userId"] as? String ?: "",
                            name = data["name"] as? String ?: "",
                            email = data["email"] as? String ?: "",
                            profileImageUrl = profileImageUrl,
                            createdAt = data["createdAt"] as? String ?: "",
                            updatedAt = data["updatedAt"] as? String ?: ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("UserRemoteDataSource", "=== ERROR FETCHING PROFILE ===")
            Log.e("UserRemoteDataSource", "User ID: $userId")
            Log.e("UserRemoteDataSource", "Error: ${e.message}")
            Log.e("UserRemoteDataSource", "Exception type: ${e.javaClass.simpleName}")
            ProfileFetchResult.FetchError(e)
        }
    }

    suspend fun deleteUser(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("profile")
            .document("main")
            .delete()
            .await()
    }
}
