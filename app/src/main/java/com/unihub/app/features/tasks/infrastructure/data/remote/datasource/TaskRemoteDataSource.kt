package com.unihub.app.features.tasks.infrastructure.data.remote.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.unihub.app.features.tasks.infrastructure.data.remote.dto.TaskDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveTask(userId: String, taskDto: TaskDto) {
        try {
            val data = mapOf(
                "id" to taskDto.id,
                "academicPeriodId" to taskDto.academicPeriodId,
                "subjectId" to taskDto.subjectId,
                "title" to taskDto.title,
                "description" to taskDto.description,
                "dueAt" to taskDto.dueAt,
                "priority" to taskDto.priority,
                "status" to taskDto.status,
                "reminderType" to taskDto.reminderType,
                "reminderValue" to taskDto.reminderValue,
                "isDeadlineReminderEnabled" to taskDto.isDeadlineReminderEnabled,
                "createdAt" to taskDto.createdAt,
                "updatedAt" to taskDto.updatedAt
            )
            
            Log.d("TaskRemoteDataSource", "=== SAVING TASK TO FIRESTORE ===")
            Log.d("TaskRemoteDataSource", "Path: users/$userId/tasks/${taskDto.id}")
            
            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskDto.id)
                .set(data)
                .await()
                
            Log.d("TaskRemoteDataSource", "Task saved successfully")
        } catch (e: Exception) {
            Log.e("TaskRemoteDataSource", "Error saving task: ${e.message}", e)
            throw e
        }
    }

    suspend fun getTasks(userId: String): List<TaskDto> {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .await()
            
            return snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                TaskDto(
                    id = data["id"] as? String ?: "",
                    academicPeriodId = data["academicPeriodId"] as? String,
                    subjectId = data["subjectId"] as? String,
                    title = data["title"] as? String ?: "",
                    description = data["description"] as? String,
                    dueAt = data["dueAt"] as? String,
                    priority = data["priority"] as? String ?: "MEDIUM",
                    status = data["status"] as? String ?: "PENDING",
                    reminderType = data["reminderType"] as? String,
                    reminderValue = (data["reminderValue"] as? Number)?.toInt(),
                    isDeadlineReminderEnabled = data["isDeadlineReminderEnabled"] as? Boolean ?: false,
                    createdAt = data["createdAt"] as? String ?: "",
                    updatedAt = data["updatedAt"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("TaskRemoteDataSource", "Error getting tasks: ${e.message}", e)
            return emptyList()
        }
    }

    suspend fun deleteTask(userId: String, taskId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("TaskRemoteDataSource", "Error deleting task: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateTaskTags(userId: String, taskId: String, tagIds: List<String>) {
        try {
            // First, delete all existing task tags
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .collection("taskTags")
                .get()
                .await()
            
            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }
            
            // Then save each tag
            tagIds.forEach { tagId ->
                val data = mapOf(
                    "taskId" to taskId,
                    "tagId" to tagId
                )
                firestore.collection("users")
                    .document(userId)
                    .collection("tasks")
                    .document(taskId)
                    .collection("taskTags")
                    .document(tagId)
                    .set(data)
                    .await()
            }
        } catch (e: Exception) {
            Log.e("TaskRemoteDataSource", "Error updating task tags: ${e.message}", e)
        }
    }

    suspend fun getTaskTags(userId: String, taskId: String): List<String> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .collection("taskTags")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.data?.get("tagId") as? String
            }
        } catch (e: Exception) {
            Log.e("TaskRemoteDataSource", "Error getting task tags: ${e.message}", e)
            emptyList()
        }
    }
}
