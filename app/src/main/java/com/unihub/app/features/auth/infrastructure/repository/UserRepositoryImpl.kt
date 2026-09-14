package com.unihub.app.features.auth.infrastructure.repository

import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.auth.infrastructure.data.local.dao.UserDao
import com.unihub.app.features.auth.infrastructure.data.mapper.toDomain
import com.unihub.app.features.auth.infrastructure.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override fun getUser(userId: String): Flow<User?> {
        return userDao.getUserById(userId).map { it?.toDomain() }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }
}
