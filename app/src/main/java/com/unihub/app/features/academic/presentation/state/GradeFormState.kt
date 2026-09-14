package com.unihub.app.features.academic.presentation.state

data class GradeFormState(
    val name: String = "",
    val nameError: String? = null,
    val value: String = "",
    val valueError: String? = null,
    val weight: String = "",
    val weightError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
