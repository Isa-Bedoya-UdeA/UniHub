package com.unihub.app.features.location.domain.model

data class Location(
    val id: String,
    val userId: String,
    val name: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val placeId: String?,
    val createdAt: String,
    val updatedAt: String
)
