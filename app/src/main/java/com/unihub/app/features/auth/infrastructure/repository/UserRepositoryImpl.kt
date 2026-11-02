package com.unihub.app.features.auth.infrastructure.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.auth.infrastructure.data.local.datasource.UserLocalDataSource
import com.unihub.app.features.auth.infrastructure.data.mapper.toDomain
import com.unihub.app.features.auth.infrastructure.data.mapper.toDto
import com.unihub.app.features.auth.infrastructure.data.mapper.toEntity
import com.unihub.app.features.auth.infrastructure.data.remote.CloudinaryDataSource
import com.unihub.app.features.auth.infrastructure.data.remote.datasource.ProfileFetchResult
import com.unihub.app.features.auth.infrastructure.data.remote.datasource.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource,
    private val cloudinaryDataSource: CloudinaryDataSource
) : UserRepository {

    override fun getUser(userId: String): Flow<User?> {
        return localDataSource.getUserById(userId).map { entity ->
            val user = entity?.toDomain()
            Log.d("UserRepositoryImpl", "=== GET USER FROM ROOM ===")
            Log.d("UserRepositoryImpl", "User ID: ${user?.userId}")
            Log.d("UserRepositoryImpl", "Profile Image URL: ${user?.profileImageUrl}")
            user
        }
    }

    override suspend fun saveUser(user: User) {
        Log.d("UserRepositoryImpl", "=== SAVE USER ===")
        Log.d("UserRepositoryImpl", "User ID: ${user.userId}")
        Log.d("UserRepositoryImpl", "Profile Image URL being saved: ${user.profileImageUrl}")
        localDataSource.insertUser(user.toEntity())
        try {
            remoteDataSource.saveUser(user.toDto())
            Log.d("UserRepositoryImpl", "Successfully saved to Firestore")
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error syncing user to Firestore: ${e.message}")
        }
    }

    override suspend fun saveUserLocalOnly(user: User) {
        Log.d("UserRepositoryImpl", "=== SAVE USER LOCAL ONLY ===")
        Log.d("UserRepositoryImpl", "User ID: ${user.userId}")
        Log.d("UserRepositoryImpl", "Profile Image URL being saved: ${user.profileImageUrl}")
        localDataSource.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        Log.d("UserRepositoryImpl", "=== UPDATE USER ===")
        Log.d("UserRepositoryImpl", "User ID: ${user.userId}")
        Log.d("UserRepositoryImpl", "Profile Image URL being updated: ${user.profileImageUrl}")
        // Use insertUser with OnConflictStrategy.REPLACE to guarantee upsert even if row wasn't present
        localDataSource.insertUser(user.toEntity())
        try {
            remoteDataSource.saveUser(user.toDto())
            Log.d("UserRepositoryImpl", "Successfully updated in Firestore")
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error updating user in Firestore: ${e.message}")
        }
    }

    override suspend fun deleteUser(user: User) {
        localDataSource.deleteUser(user.toEntity())
        try {
            remoteDataSource.deleteUser(user.userId)
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error deleting user from Firestore: ${e.message}")
        }
    }

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val url = cloudinaryDataSource.uploadProfileImage(imageUri)
            Result.success(url)
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error uploading profile image: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun syncProfile(userId: String) {
        Log.d("UserRepositoryImpl", "=== SYNC PROFILE FROM FIRESTORE ===")
        Log.d("UserRepositoryImpl", "User ID: $userId")
        try {
            val result = remoteDataSource.getUser(userId)
            if (result is ProfileFetchResult.Found) {
                Log.d("UserRepositoryImpl", "Profile found in Firestore")
                val domainUser = result.user.toDomain()
                localDataSource.insertUser(domainUser.toEntity())
                Log.d("UserRepositoryImpl", "Successfully synced profile to Room")
            } else if (result is ProfileFetchResult.NotFound) {
                Log.d("UserRepositoryImpl", "Profile not found in Firestore, checking local or Firebase Auth")
                val localEntity = localDataSource.getUserById(userId).first()
                if (localEntity != null) {
                    remoteDataSource.saveUser(localEntity.toDomain().toDto())
                } else {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    if (firebaseUser != null && firebaseUser.uid == userId) {
                        val now = Instant.now().toString()
                        val fallback = User(
                            userId = userId,
                            name = firebaseUser.displayName?.ifBlank { null } ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            profileImageUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = now,
                            updatedAt = now
                        )
                        localDataSource.insertUser(fallback.toEntity())
                        remoteDataSource.saveUser(fallback.toDto())
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error syncing profile from Firestore: ${e.message}")
        }
    }
}
