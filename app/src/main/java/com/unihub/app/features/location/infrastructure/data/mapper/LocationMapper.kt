package com.unihub.app.features.location.infrastructure.data.mapper

import com.unihub.app.features.location.domain.model.Location
import com.unihub.app.features.location.infrastructure.data.local.entity.LocationEntity

fun LocationEntity.toDomain(): Location {
    return Location(
        id = id,
        userId = userId,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeId = placeId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        userId = userId,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeId = placeId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
