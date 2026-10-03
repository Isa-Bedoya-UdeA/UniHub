package com.unihub.app.features.auth.infrastructure.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.auth.infrastructure.data.remote.FirebaseAuthDataSource
import com.unihub.app.features.auth.infrastructure.data.remote.datasource.ProfileFetchResult
import com.unihub.app.features.auth.infrastructure.data.remote.datasource.UserRemoteDataSource
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.location.domain.repository.LocationRepository
import com.unihub.app.features.settings.domain.repository.SettingsRepository
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val studyRepository: StudyRepository,
    private val academicRepository: AcademicRepository,
    private val subjectRepository: SubjectRepository,
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository
) : AuthRepository {

    override fun observeAuthState(): Flow<AuthState> {
        return authDataSource.observeAuthState().map { isSignedIn ->
            if (isSignedIn) {
                val uid = authDataSource.getCurrentUid()
                if (uid != null) AuthState.Authenticated(uid)
                else AuthState.Unauthenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    override fun getCurrentUid(): String? = authDataSource.getCurrentUid()

    override suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val result = authDataSource.signInWithGoogle(idToken)
            val firebaseUser = result.user ?: return Result.failure(Exception("No se pudo obtener el usuario"))
            val uid = firebaseUser.uid
            val now = Instant.now().toString()

            Log.d("AuthRepositoryImpl", "=== SIGN IN WITH GOOGLE ===")
            Log.d("AuthRepositoryImpl", "Firebase UID: $uid")
            Log.d("AuthRepositoryImpl", "Firebase Email: ${firebaseUser.email}")
            Log.d("AuthRepositoryImpl", "Firebase PhotoURL: ${firebaseUser.photoUrl}")

            val remoteResult = userRemoteDataSource.getUser(uid)
            Log.d("AuthRepositoryImpl", "Remote profile fetch result type: ${remoteResult::class.simpleName}")

            when (remoteResult) {
                is ProfileFetchResult.Found -> {
                    val remoteProfile = remoteResult.user
                    Log.d("AuthRepositoryImpl", "=== PROFILE FOUND IN FIRESTORE ===")
                    Log.d("AuthRepositoryImpl", "Remote profileImageUrl: ${remoteProfile.profileImageUrl}")
                    Log.d("AuthRepositoryImpl", "Remote name: ${remoteProfile.name}")
                    
                    val user = User(
                        userId = remoteProfile.userId,
                        name = remoteProfile.name,
                        email = remoteProfile.email.ifEmpty { firebaseUser.email ?: "" },
                        profileImageUrl = remoteProfile.profileImageUrl,
                        createdAt = remoteProfile.createdAt,
                        updatedAt = now
                    )
                    Log.d("AuthRepositoryImpl", "Saving user with profileImageUrl: ${user.profileImageUrl}")
                    userRepository.saveUser(user)
                    syncAllUserData(uid)
                }

                is ProfileFetchResult.NotFound -> {
                    Log.d("AuthRepositoryImpl", "=== PROFILE NOT FOUND IN FIRESTORE ===")
                    val localUser = userRepository.getUser(uid).first()
                    Log.d("AuthRepositoryImpl", "Local user exists: ${localUser != null}")
                    Log.d("AuthRepositoryImpl", "Local user profileImageUrl: ${localUser?.profileImageUrl}")
                    
                    val user = if (localUser != null) {
                        localUser.copy(
                            email = firebaseUser.email ?: localUser.email,
                            profileImageUrl = localUser.profileImageUrl
                                ?: firebaseUser.photoUrl?.toString(),
                            updatedAt = now
                        )
                    } else {
                        User(
                            userId = uid,
                            name = firebaseUser.displayName ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            profileImageUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = now,
                            updatedAt = now
                        )
                    }
                    Log.d("AuthRepositoryImpl", "Saving user with profileImageUrl: ${user.profileImageUrl}")
                    userRepository.saveUser(user)
                    syncAllUserData(uid)
                }

                is ProfileFetchResult.FetchError -> {
                    Log.e("AuthRepositoryImpl", "=== PROFILE FETCH ERROR ===")
                    Log.e("AuthRepositoryImpl", "Error: ${remoteResult.exception.message}")
                    Log.w("AuthRepositoryImpl", "Network error fetching profile, using local data only")
                    val localUser = userRepository.getUser(uid).first()
                    Log.d("AuthRepositoryImpl", "Local user exists: ${localUser != null}")
                    Log.d("AuthRepositoryImpl", "Local user profileImageUrl: ${localUser?.profileImageUrl}")
                    
                    val user = if (localUser != null) {
                        localUser.copy(
                            email = firebaseUser.email ?: localUser.email,
                            updatedAt = now
                        )
                    } else {
                        User(
                            userId = uid,
                            name = firebaseUser.displayName ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            profileImageUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = now,
                            updatedAt = now
                        )
                    }
                    Log.d("AuthRepositoryImpl", "Saving user LOCAL ONLY with profileImageUrl: ${user.profileImageUrl}")
                    userRepository.saveUserLocalOnly(user)
                }
            }

            Result.success(uid)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Sign in error: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun syncAllUserData(userId: String) {
        try {
            userRepository.syncProfile(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync profile error: ${e.message}")
        }
        try {
            studyRepository.syncStudies(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync studies error: ${e.message}")
        }
        try {
            academicRepository.syncAcademicData(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync academic data error: ${e.message}")
        }
        try {
            subjectRepository.syncSubjects(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync subjects error: ${e.message}")
        }
        try {
            eventRepository.syncEvents(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync events error: ${e.message}")
        }
        try {
            taskRepository.syncTasks(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync tasks error: ${e.message}")
        }
        try {
            tagRepository.syncTags(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync tags error: ${e.message}")
        }
        try {
            locationRepository.syncLocations(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync locations error: ${e.message}")
        }
        try {
            settingsRepository.syncPreferences(userId)
        } catch (e: Exception) {
            Log.w("AuthRepositoryImpl", "Sync preferences error: ${e.message}")
        }
    }

    override suspend fun signOut() {
        authDataSource.signOut()
    }

    override suspend fun syncExistingUser(userId: String) {
        Log.d("AuthRepositoryImpl", "=== SYNC EXISTING USER ===")
        Log.d("AuthRepositoryImpl", "User ID: $userId")
        
        val remoteResult = userRemoteDataSource.getUser(userId)
        Log.d("AuthRepositoryImpl", "Remote profile fetch result type: ${remoteResult::class.simpleName}")

        when (remoteResult) {
            is ProfileFetchResult.Found -> {
                val remoteProfile = remoteResult.user
                Log.d("AuthRepositoryImpl", "=== PROFILE FOUND IN FIRESTORE ===")
                Log.d("AuthRepositoryImpl", "Remote profileImageUrl: ${remoteProfile.profileImageUrl}")
                
                val user = User(
                    userId = remoteProfile.userId,
                    name = remoteProfile.name,
                    email = remoteProfile.email,
                    profileImageUrl = remoteProfile.profileImageUrl,
                    createdAt = remoteProfile.createdAt,
                    updatedAt = remoteProfile.updatedAt
                )
                Log.d("AuthRepositoryImpl", "Saving user with profileImageUrl: ${user.profileImageUrl}")
                userRepository.saveUserLocalOnly(user)
                syncAllUserData(userId)
            }

            is ProfileFetchResult.NotFound -> {
                Log.d("AuthRepositoryImpl", "=== PROFILE NOT FOUND IN FIRESTORE ===")
            }

            is ProfileFetchResult.FetchError -> {
                Log.e("AuthRepositoryImpl", "=== PROFILE FETCH ERROR ===")
                Log.e("AuthRepositoryImpl", "Error: ${remoteResult.exception.message}")
            }
        }
    }
}
