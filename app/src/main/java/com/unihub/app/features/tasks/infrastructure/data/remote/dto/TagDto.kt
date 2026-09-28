package com.unihub.app.features.tasks.infrastructure.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val createdAt: String = ""
)
