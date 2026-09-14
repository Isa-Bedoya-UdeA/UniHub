package com.unihub.app.features.academic.domain.model

data class Study(
    val id: String,
    val userId: String,
    val name: String,
    val institution: String,
    val totalCredits: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
