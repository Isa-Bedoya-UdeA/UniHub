package com.unihub.server.models

import kotlinx.serialization.Serializable

@Serializable
data class AiParseRequest(
    val prompt: String,
    val context: String? = null
)

@Serializable
data class AiParseResponse(
    val action: String,
    val rawPrompt: String,
    val structuredData: Map<String, String> = emptyMap(),
    val reply: String
)
