package com.unihub.app.features.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AiActionType {
    CHAT,
    CREATE_EVENT,
    CREATE_TASK,
    CREATE_SUBJECT,
    REGISTER_GRADE,
    PLANNING_RECOMMENDATION,
    ACADEMIC_ASSISTANT,
    CLARIFICATION
}
