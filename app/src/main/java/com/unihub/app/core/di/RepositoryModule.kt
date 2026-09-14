package com.unihub.app.core.di

import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.domain.repository.StudyRepository
import com.unihub.app.features.academic.infrastructure.repository.AcademicRepositoryImpl
import com.unihub.app.features.academic.infrastructure.repository.StudyRepositoryImpl
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.events.infrastructure.repository.EventRepositoryImpl
import com.unihub.app.features.location.domain.repository.LocationRepository
import com.unihub.app.features.location.infrastructure.repository.LocationRepositoryImpl
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import com.unihub.app.features.subjects.infrastructure.repository.SubjectRepositoryImpl
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.infrastructure.repository.TaskRepositoryImpl
import com.unihub.app.features.auth.domain.repository.UserRepository
import com.unihub.app.features.auth.infrastructure.repository.UserRepositoryImpl
import com.unihub.app.features.tasks.domain.repository.TagRepository
import com.unihub.app.features.tasks.infrastructure.repository.TagRepositoryImpl
import com.unihub.app.features.settings.domain.repository.SettingsRepository
import com.unihub.app.features.settings.infrastructure.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindStudyRepository(impl: StudyRepositoryImpl): StudyRepository

    @Binds
    @Singleton
    abstract fun bindAcademicRepository(impl: AcademicRepositoryImpl): AcademicRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
