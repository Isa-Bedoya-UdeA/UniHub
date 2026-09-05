package com.unihub.app.features.location.infrastructure.data.local.dao

import androidx.room.*
import com.unihub.app.features.location.infrastructure.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM Location WHERE user_id = :userId")
    fun getLocations(userId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM Location WHERE location_id = :id")
    fun getLocationById(id: String): Flow<LocationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("DELETE FROM Location WHERE location_id = :id")
    suspend fun deleteLocation(id: String)
}
