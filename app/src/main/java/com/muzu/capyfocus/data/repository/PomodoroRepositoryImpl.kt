package com.muzu.capyfocus.data.repository

import com.muzu.capyfocus.data.local.room.PomodoroDao
import com.muzu.capyfocus.data.mappers.toDomain
import com.muzu.capyfocus.data.mappers.toEntity
import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.models.PomodoroSession
import com.muzu.capyfocus.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepositoryImpl @Inject constructor(
    private val pomodoroDao: PomodoroDao
) : PomodoroRepository {
    override fun observeSessions(): Flow<List<PomodoroSession>> {
        return pomodoroDao.observeSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSession(session: PomodoroSession) {
        pomodoroDao.insertSession(session.toEntity())
    }

    override fun observeConfig(): Flow<PomodoroConfig> {
        return pomodoroDao.observeConfig().map { entity ->
            entity?.toDomain() ?: PomodoroConfig()
        }
    }

    override suspend fun updateConfig(config: PomodoroConfig) {
        pomodoroDao.upsertConfig(config.toEntity())
    }
}
