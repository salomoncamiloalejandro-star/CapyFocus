package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.PomodoroSession
import com.muzu.capyfocus.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePomodoroSessionsUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    operator fun invoke(): Flow<List<PomodoroSession>> {
        return repository.observeSessions()
    }
}
