package com.unihub.app.features.location.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val id: String,
    val userId: String,
    val name: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val placeId: String?,
    val createdAt: Long,
    val updatedAt: Long
)
