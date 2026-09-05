package com.unihub.app.features.location.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val id: String = "",
    val name: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val placeId: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
