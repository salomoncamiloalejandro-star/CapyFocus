package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.core.time.TimeProvider
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.repository.AgendaRepository
import javax.inject.Inject

class UpdateAgendaItemUseCase @Inject constructor(
    private val repository: AgendaRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(item: AgendaItem): Result<AgendaItem> {
        if (item.title.isBlank()) {
            return Result.failure(IllegalArgumentException("El título no puede estar vacío"))
        }

        val now = timeProvider.currentTimeMillis()
        val updatedItem = item.copy(updatedAt = now)
        repository.upsertItem(updatedItem)
        return Result.success(updatedItem)
    }
}
