package com.muzu.capyfocus.core.di

import com.muzu.capyfocus.core.time.SystemTimeProvider
import com.muzu.capyfocus.core.time.TimeProvider
import com.muzu.capyfocus.data.repository.AgendaRepositoryImpl
import com.muzu.capyfocus.data.repository.PomodoroRepositoryImpl
import com.muzu.capyfocus.data.repository.SubjectRepositoryImpl
import com.muzu.capyfocus.domain.repository.AgendaRepository
import com.muzu.capyfocus.domain.repository.PomodoroRepository
import com.muzu.capyfocus.domain.repository.SubjectRepository
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
    abstract fun bindSubjectRepository(
        impl: SubjectRepositoryImpl,
    ): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindAgendaRepository(
        impl: AgendaRepositoryImpl,
    ): AgendaRepository

    @Binds
    @Singleton
    abstract fun bindPomodoroRepository(
        impl: PomodoroRepositoryImpl,
    ): PomodoroRepository

    @Binds
    @Singleton
    abstract fun bindTimeProvider(
        impl: SystemTimeProvider,
    ): TimeProvider
}
