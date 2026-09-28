package com.unihub.app.features.auth.infrastructure.data.remote

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun uploadProfileImage(imageUri: Uri): String {
        val mimeType = context.contentResolver.getType(imageUri)
            ?: throw IllegalArgumentException("Unable to determine MIME type")

        if (!SUPPORTED_MIME_TYPES.contains(mimeType)) {
            throw IllegalArgumentException("Unsupported image format: $mimeType")
        }

        val fileSize = getFileSize(imageUri)
        if (fileSize > MAX_FILE_SIZE) {
            throw IllegalArgumentException("File size exceeds maximum allowed size of 5 MB")
        }

        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Unable to read image file")

        val fileBytes = inputStream.use { it.readBytes() }
        val fileName = getFileName(imageUri) ?: "profile_image.jpg"

        val response = httpClient.submitFormWithBinaryData(
            url = UPLOAD_URL,
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
                append("upload_preset", UPLOAD_PRESET)
                append("folder", FOLDER)
            }
        )

        val responseBody = response.bodyAsText()
        val jsonResponse = json.decodeFromString<JsonObject>(responseBody)

        return jsonResponse["secure_url"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("Cloudinary response missing secure_url")
    }

    private fun getFileSize(uri: Uri): Long {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex >= 0) it.getLong(sizeIndex) else 0L
            } else 0L
        } ?: 0L
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) it.getString(nameIndex) else null
            } else null
        }
    }

    companion object {
        private const val CLOUD_NAME = "c6ff8iyp"
        private const val UPLOAD_PRESET = "unihub_profile_images"
        private const val FOLDER = "unihub/profile-images"
        private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
        private const val MAX_FILE_SIZE = 5L * 1024 * 1024
        private val SUPPORTED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/webp"
        )
    }
}
