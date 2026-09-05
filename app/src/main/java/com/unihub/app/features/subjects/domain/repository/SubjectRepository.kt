package com.unihub.app.features.subjects.domain.repository

import com.unihub.app.features.subjects.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>>
    fun getSubjectById(id: String): Flow<Subject?>
    suspend fun saveSubject(subject: Subject)
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(id: String)
    suspend fun syncSubjects(userId: String)
}
