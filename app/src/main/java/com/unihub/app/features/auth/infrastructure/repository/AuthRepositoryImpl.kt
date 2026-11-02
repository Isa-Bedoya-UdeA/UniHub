package com.unihub.app.features.auth.infrastructure.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.auth.domain.model.AuthState
import com.unihub.app.features.auth.domain.model.User
import com.unihub.app.features.auth.domain.repository.AuthRepository
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.auth.infrastructure.data.mapper.toDto
import com.unihub.app.features.auth.infrastructure.data.remote.FirebaseAuthDataSource
import com.unihub.app.features.auth.infrastructure.data.remote.datasource.ProfileFetchResult
import com.unihub.app.features.auth.infrastructure.data.remote.datasource.UserRemoteDataSource
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.location.domain.repository.LocationRepository
import com.unihub.app.features.settings.domain.repository.SettingsRepository
import com.unihub.app.features.academic.domain.repository.SubjectRepository
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.domain.repository.TagRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
            Log.d("AuthRepositoryImpl", "Firebase DisplayName: ${firebaseUser.displayName}")

            withContext(NonCancellable) {
                // 1. Immediately save initial Google user to Room so the app has local user data without delay
                val initialUser = User(
                    userId = uid,
                    name = firebaseUser.displayName?.ifBlank { null } ?: "Usuario",
                    email = firebaseUser.email ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = now,
                    updatedAt = now
                )
                userRepository.saveUserLocalOnly(initialUser)
                Log.d("AuthRepositoryImpl", "Saved initial Google user to Room: ${initialUser.name}")

                // 2. Fetch remote profile from Firestore
                val remoteResult = userRemoteDataSource.getUser(uid)
                Log.d("AuthRepositoryImpl", "Remote profile fetch result type: ${remoteResult::class.simpleName}")

                when (remoteResult) {
                    is ProfileFetchResult.Found -> {
                        val remoteProfile = remoteResult.user
                        Log.d("AuthRepositoryImpl", "=== PROFILE FOUND IN FIRESTORE ===")
                        val user = User(
                            userId = remoteProfile.userId,
                            name = remoteProfile.name.ifBlank { initialUser.name },
                            email = remoteProfile.email.ifBlank { initialUser.email },
                            profileImageUrl = remoteProfile.profileImageUrl ?: initialUser.profileImageUrl,
                            createdAt = remoteProfile.createdAt.ifBlank { now },
                            updatedAt = now
                        )
                        Log.d("AuthRepositoryImpl", "Saving merged user with profileImageUrl: ${user.profileImageUrl}")
                        userRepository.saveUser(user)
                        syncAllUserData(uid)
                    }

                    is ProfileFetchResult.NotFound -> {
                        Log.d("AuthRepositoryImpl", "=== PROFILE NOT FOUND IN FIRESTORE - CREATING NEW ===")
                        userRepository.saveUser(initialUser)
                        syncAllUserData(uid)
                    }

                    is ProfileFetchResult.FetchError -> {
                        Log.e("AuthRepositoryImpl", "=== PROFILE FETCH ERROR: ${remoteResult.exception.message} ===")
                        try {
                            userRemoteDataSource.saveUser(initialUser.toDto())
                        } catch (e: Exception) {
                            Log.w("AuthRepositoryImpl", "Could not save user to Firestore: ${e.message}")
                        }
                    }
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
        withContext(NonCancellable) {
            Log.d("AuthRepositoryImpl", "=== SYNC EXISTING USER ===")
            Log.d("AuthRepositoryImpl", "User ID: $userId")
            
            val remoteResult = userRemoteDataSource.getUser(userId)
            val firebaseUser = firebaseAuth.currentUser
            Log.d("AuthRepositoryImpl", "Remote profile fetch result type: ${remoteResult::class.simpleName}")

            when (remoteResult) {
                is ProfileFetchResult.Found -> {
                    val remoteProfile = remoteResult.user
                    Log.d("AuthRepositoryImpl", "=== PROFILE FOUND IN FIRESTORE ===")
                    Log.d("AuthRepositoryImpl", "Remote profileImageUrl: ${remoteProfile.profileImageUrl}")
                    
                    val user = User(
                        userId = remoteProfile.userId,
                        name = remoteProfile.name.ifBlank { firebaseUser?.displayName ?: "Usuario" },
                        email = remoteProfile.email.ifBlank { firebaseUser?.email ?: "" },
                        profileImageUrl = remoteProfile.profileImageUrl ?: firebaseUser?.photoUrl?.toString(),
                        createdAt = remoteProfile.createdAt,
                        updatedAt = remoteProfile.updatedAt
                    )
                    Log.d("AuthRepositoryImpl", "Saving user with profileImageUrl: ${user.profileImageUrl}")
                    userRepository.saveUserLocalOnly(user)
                    syncAllUserData(userId)
                }

                is ProfileFetchResult.NotFound -> {
                    Log.d("AuthRepositoryImpl", "=== PROFILE NOT FOUND IN FIRESTORE ===")
                    if (firebaseUser != null && firebaseUser.uid == userId) {
                        val now = Instant.now().toString()
                        val user = User(
                            userId = userId,
                            name = firebaseUser.displayName?.ifBlank { null } ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            profileImageUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = now,
                            updatedAt = now
                        )
                        Log.d("AuthRepositoryImpl", "Creating missing profile from Firebase Auth: ${user.name}")
                        userRepository.saveUser(user)
                        syncAllUserData(userId)
                    }
                }

                is ProfileFetchResult.FetchError -> {
                    Log.e("AuthRepositoryImpl", "=== PROFILE FETCH ERROR ===")
                    Log.e("AuthRepositoryImpl", "Error: ${remoteResult.exception.message}")
                    val localUser = userRepository.getUser(userId).first()
                    if (localUser == null && firebaseUser != null && firebaseUser.uid == userId) {
                        val now = Instant.now().toString()
                        val user = User(
                            userId = userId,
                            name = firebaseUser.displayName?.ifBlank { null } ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            profileImageUrl = firebaseUser.photoUrl?.toString(),
                            createdAt = now,
                            updatedAt = now
                        )
                        userRepository.saveUserLocalOnly(user)
                    }
                }
            }
        }
    }
}
