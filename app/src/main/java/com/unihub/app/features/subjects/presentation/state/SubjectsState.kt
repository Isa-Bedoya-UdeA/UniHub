package com.unihub.app.features.subjects.presentation.state

import com.unihub.app.core.designsystem.component.academic.StudyOption
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.subjects.domain.model.Subject

data class SubjectsState(
    val studies: List<StudyOption> = emptyList(),
    val selectedStudyId: String? = null,
    val periods: List<AcademicPeriod> = emptyList(),
    val selectedPeriodId: String? = null,
    val subjects: List<Subject> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
