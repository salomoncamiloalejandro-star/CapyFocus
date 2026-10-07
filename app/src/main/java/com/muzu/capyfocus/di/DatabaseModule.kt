package com.muzu.capyfocus.di

import android.content.Context
import androidx.room.Room
import com.muzu.capyfocus.data.local.room.AgendaItemDao
import com.muzu.capyfocus.data.local.room.AppDatabase
import com.muzu.capyfocus.data.local.room.SubjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "capyfocus.db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    fun provideAgendaItemDao(database: AppDatabase): AgendaItemDao {
        return database.agendaItemDao()
    }
}
