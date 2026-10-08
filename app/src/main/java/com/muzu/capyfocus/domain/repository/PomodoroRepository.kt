package com.muzu.capyfocus.domain.repository

import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.models.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun observeSessions(): Flow<List<PomodoroSession>>
    suspend fun insertSession(session: PomodoroSession)
    fun observeConfig(): Flow<PomodoroConfig>
    suspend fun updateConfig(config: PomodoroConfig)
}
