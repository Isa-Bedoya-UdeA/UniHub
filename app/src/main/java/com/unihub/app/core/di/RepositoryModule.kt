package com.unihub.app.core.di

import com.unihub.app.features.academic.domain.repository.AcademicRepository
import com.unihub.app.features.academic.infrastructure.repository.AcademicRepositoryImpl
import com.unihub.app.features.events.domain.repository.EventRepository
import com.unihub.app.features.events.infrastructure.repository.EventRepositoryImpl
import com.unihub.app.features.location.domain.repository.LocationRepository
import com.unihub.app.features.location.infrastructure.repository.LocationRepositoryImpl
import com.unihub.app.features.subjects.domain.repository.SubjectRepository
import com.unihub.app.features.subjects.infrastructure.repository.SubjectRepositoryImpl
import com.unihub.app.features.tasks.domain.repository.TaskRepository
import com.unihub.app.features.tasks.infrastructure.repository.TaskRepositoryImpl
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
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindAcademicRepository(impl: AcademicRepositoryImpl): AcademicRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
