package com.unihub.app.features.academic.domain.model

data class Study(
    val id: String,
    val userId: String,
    val name: String,
    val institution: String,
    val totalCredits: Int,
    val approvedCredits: Int? = null,
    val cumulativeGpa: Double? = null,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
