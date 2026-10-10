package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
)
