package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.repository.AgendaRepository
import javax.inject.Inject

class DeleteAgendaItemUseCase @Inject constructor(
    private val repository: AgendaRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.deleteItem(id)
    }
}
