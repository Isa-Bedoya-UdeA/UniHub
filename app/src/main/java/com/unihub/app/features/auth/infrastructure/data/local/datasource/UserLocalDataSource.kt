package com.unihub.app.features.auth.infrastructure.data.local.datasource

import com.unihub.app.features.auth.infrastructure.data.local.dao.UserDao
import com.unihub.app.features.auth.infrastructure.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao
) {
    fun getUserById(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)
}
