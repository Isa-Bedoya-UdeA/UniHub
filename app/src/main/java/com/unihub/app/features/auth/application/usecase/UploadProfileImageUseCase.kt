package com.unihub.app.features.auth.application.usecase

import android.net.Uri
import com.unihub.app.features.auth.domain.repository.UserRepository
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, imageUri: Uri): Result<String> {
        return repository.uploadProfileImage(userId, imageUri)
    }
}
