package com.unihub.app.features.subjects.infrastructure.repository

import com.unihub.app.features.subjects.domain.model.Subject
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import com.unihub.app.features.subjects.infrastructure.data.local.dao.SubjectDao
import com.unihub.app.features.subjects.infrastructure.data.mapper.toDomain
import com.unihub.app.features.subjects.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
) : SubjectRepository {

    override fun getSubjects(userId: String, academicPeriodId: String): Flow<List<Subject>> {
        return subjectDao.getSubjects(userId, academicPeriodId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllSubjects(userId: String): Flow<List<Subject>> {
        return subjectDao.getAllSubjects(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubjectById(id: String): Flow<Subject?> {
        return subjectDao.getSubjectById(id).map { it?.toDomain() }
    }

    override suspend fun saveSubject(subject: Subject) {
        subjectDao.insertSubject(subject.toEntity())
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.insertSubject(subject.toEntity())
    }

    override suspend fun deleteSubject(id: String) {
        subjectDao.deleteSubject(id)
    }

    override suspend fun syncSubjects(userId: String) {
        // To be implemented with Ktor/Firestore
    }
}
