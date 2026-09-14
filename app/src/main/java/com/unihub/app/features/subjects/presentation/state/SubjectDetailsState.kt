package com.unihub.app.features.subjects.presentation.state

import com.unihub.app.features.academic.domain.model.Grade
import com.unihub.app.features.events.domain.model.Event
import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.tasks.domain.model.Task

data class SubjectDetailsState(
    val subject: Subject? = null,
    val grades: List<Grade> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val events: List<Event> = emptyList(),
    val nextClass: Event? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
