package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.core.time.TimeProvider
import com.muzu.capyfocus.domain.repository.AgendaRepository
import javax.inject.Inject

class ToggleAgendaItemCompletionUseCase @Inject constructor(
    private val repository: AgendaRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(id: String, isCompleted: Boolean) {
        val now = timeProvider.currentTimeMillis()
        repository.toggleCompleted(id, isCompleted, now)
    }
}
