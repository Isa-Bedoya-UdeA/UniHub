package com.unihub.app.features.academic.infrastructure.data.local.datasource

import com.unihub.app.features.academic.infrastructure.data.local.dao.SubjectDao
import com.unihub.app.features.academic.infrastructure.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectLocalDataSource @Inject constructor(
    private val subjectDao: SubjectDao
) {
    fun getSubjects(userId: String, academicPeriodId: String): Flow<List<SubjectEntity>> =
        subjectDao.getSubjects(userId, academicPeriodId)

    fun getAllSubjects(userId: String): Flow<List<SubjectEntity>> =
        subjectDao.getAllSubjects(userId)

    fun getSubjectById(id: String): Flow<SubjectEntity?> =
        subjectDao.getSubjectById(id)

    suspend fun insertSubject(subject: SubjectEntity) =
        subjectDao.insertSubject(subject)

    suspend fun deleteSubject(id: String) =
        subjectDao.deleteSubject(id)
}
