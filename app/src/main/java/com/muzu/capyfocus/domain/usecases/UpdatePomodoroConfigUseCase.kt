package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.repository.PomodoroRepository
import javax.inject.Inject

class UpdatePomodoroConfigUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(config: PomodoroConfig) {
        repository.updateConfig(config)
    }
}
