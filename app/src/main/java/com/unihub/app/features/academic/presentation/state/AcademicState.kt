package com.unihub.app.features.academic.presentation.state

import com.unihub.app.core.designsystem.component.academic.StudyOption
import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.AcademicSummary
import com.unihub.app.features.academic.presentation.viewmodel.SubjectWithGrade

data class AcademicState(
    val studies: List<StudyOption> = emptyList(),
    val selectedStudyId: String? = null,
    val summary: AcademicSummary = AcademicSummary(0.0, 0.0, 0, 0, 0.0),
    val subjects: List<SubjectWithGrade> = emptyList(),
    val periods: List<AcademicPeriod> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
