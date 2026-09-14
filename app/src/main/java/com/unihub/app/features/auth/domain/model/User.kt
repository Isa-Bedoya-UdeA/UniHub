package com.unihub.app.features.auth.domain.model

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
