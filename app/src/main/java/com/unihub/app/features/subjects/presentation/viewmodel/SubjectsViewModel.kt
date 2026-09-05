package com.unihub.app.features.subjects.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unihub.app.features.subjects.application.usecase.DeleteSubjectUseCase
import com.unihub.app.features.subjects.application.usecase.GetSubjectsUseCase
import com.unihub.app.features.subjects.domain.model.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    getSubjectsUseCase: GetSubjectsUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    // For now hardcoded user/period. This will come from Auth/Session later.
    val subjects: StateFlow<List<Subject>> = getSubjectsUseCase("user123", "2026-2")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteSubject(id: String) {
        viewModelScope.launch {
            deleteSubjectUseCase(id)
        }
    }
}
