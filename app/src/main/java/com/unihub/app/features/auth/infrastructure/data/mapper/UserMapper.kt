package com.unihub.app.features.auth.infrastructure.data.mapper

import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.infrastructure.data.local.entity.UserEntity
import com.unihub.app.features.auth.infrastructure.data.remote.dto.UserDto

fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UserEntity.toDomain(): User {
    return User(
        userId = userId,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UserDto.toDomain(): User {
    return User(
        userId = userId,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        userId = userId,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt.toLongOrNull() ?: 0L,
        updatedAt = updatedAt.toLongOrNull() ?: 0L
    )
}

