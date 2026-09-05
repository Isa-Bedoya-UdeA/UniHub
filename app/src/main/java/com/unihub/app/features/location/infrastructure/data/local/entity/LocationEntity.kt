package com.unihub.app.features.location.infrastructure.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
data class LocationEntity(
    @PrimaryKey
    @ColumnInfo(name = "location_id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "name")
    val name: String?,
    
    @ColumnInfo(name = "address")
    val address: String?,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double?,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double?,
    
    @ColumnInfo(name = "place_id")
    val placeId: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
