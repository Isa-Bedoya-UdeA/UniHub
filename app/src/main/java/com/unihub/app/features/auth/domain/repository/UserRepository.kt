package com.unihub.app.features.auth.domain.repository

import android.net.Uri
import com.unihub.app.features.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun saveUserLocalOnly(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String>
    suspend fun syncProfile(userId: String)
}
