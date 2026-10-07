package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.repository.AgendaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAgendaForDateRangeUseCase @Inject constructor(
    private val repository: AgendaRepository,
) {
    operator fun invoke(startEpochDay: Long, endEpochDay: Long): Flow<List<AgendaItem>> {
        return repository.observeItemsForDateRange(startEpochDay, endEpochDay)
    }
}
