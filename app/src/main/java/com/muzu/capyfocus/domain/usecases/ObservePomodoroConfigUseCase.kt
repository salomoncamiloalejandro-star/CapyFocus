package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePomodoroConfigUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    operator fun invoke(): Flow<PomodoroConfig> {
        return repository.observeConfig()
    }
}
