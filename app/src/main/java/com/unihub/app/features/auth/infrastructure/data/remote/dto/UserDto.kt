package com.unihub.app.features.auth.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val userId: String,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long
)
