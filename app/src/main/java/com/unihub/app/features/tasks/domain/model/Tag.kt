package com.unihub.app.features.tasks.domain.model

data class Tag(
    val id: String,
    val userId: String,
    val name: String,
    val createdAt: String
)

data class TaskTag(
    val taskId: String,
    val tagId: String
)
