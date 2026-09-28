package com.unihub.app.features.location.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.location.infrastructure.data.remote.dto.LocationDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveLocation(userId: String, locationDto: LocationDto) {
        try {
            val data = mapOf(
                "id" to locationDto.id,
                "userId" to locationDto.userId,
                "name" to locationDto.name,
                "address" to locationDto.address,
                "latitude" to locationDto.latitude,
                "longitude" to locationDto.longitude,
                "placeId" to locationDto.placeId,
                "createdAt" to locationDto.createdAt,
                "updatedAt" to locationDto.updatedAt
            )
            
            Log.d("LocationRemoteDataSource", "=== SAVING LOCATION TO FIRESTORE ===")
            Log.d("LocationRemoteDataSource", "Path: users/$userId/locations/${locationDto.id}")
            
            firestore.collection("users")
                .document(userId)
                .collection("locations")
                .document(locationDto.id)
                .set(data)
                .await()
                
            Log.d("LocationRemoteDataSource", "Location saved successfully")
        } catch (e: Exception) {
            Log.e("LocationRemoteDataSource", "Error saving location: ${e.message}", e)
            throw e
        }
    }

    suspend fun getLocations(userId: String): List<LocationDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("locations")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                LocationDto(
                    id = data["id"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    name = data["name"] as? String,
                    address = data["address"] as? String,
                    latitude = data["latitude"] as? Double,
                    longitude = data["longitude"] as? Double,
                    placeId = data["placeId"] as? String,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("LocationRemoteDataSource", "Error getting locations: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteLocation(userId: String, locationId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("locations")
                .document(locationId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("LocationRemoteDataSource", "Error deleting location: ${e.message}", e)
            throw e
        }
    }
}
