package com.muzu.capyfocus.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SubjectEntity::class, AgendaItemEntity::class, PomodoroSessionEntity::class, PomodoroConfigEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun agendaItemDao(): AgendaItemDao
    abstract fun pomodoroDao(): PomodoroDao
}
