package com.muzu.capyfocus.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY timestampEpochMilli DESC")
    fun observeSessions(): Flow<List<PomodoroSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSessionEntity)

    @Query("SELECT * FROM pomodoro_config WHERE id = 1")
    fun observeConfig(): Flow<PomodoroConfigEntity?>

    @Upsert
    suspend fun upsertConfig(config: PomodoroConfigEntity)
}
