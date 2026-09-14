package com.unihub.app.features.subjects.presentation.state

import com.unihub.app.features.academic.domain.model.AcademicPeriod
import com.unihub.app.features.academic.domain.model.Study

data class SubjectFormState(
    val name: String = "",
    val nameError: String? = null,
    val code: String = "",
    val codeError: String? = null,
    val professor: String = "",
    val professorError: String? = null,
    val credits: String = "",
    val creditsError: String? = null,
    val academicPeriodId: String? = null,
    val academicPeriodError: String? = null,
    val studyId: String? = null,
    val studyError: String? = null,
    val periods: List<AcademicPeriod> = emptyList(),
    val studies: List<Study> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
