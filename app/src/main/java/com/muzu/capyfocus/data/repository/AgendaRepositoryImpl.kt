package com.muzu.capyfocus.data.repository

import com.muzu.capyfocus.data.local.room.AgendaItemDao
import com.muzu.capyfocus.data.mappers.toAgendaItem
import com.muzu.capyfocus.data.mappers.toEntity
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.repository.AgendaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgendaRepositoryImpl @Inject constructor(
    private val agendaItemDao: AgendaItemDao,
) : AgendaRepository {

    override fun observeItemsUpToEndDate(endEpochDay: Long): Flow<List<AgendaItem>> {
        return agendaItemDao.observeItemsUpToEndDate(endEpochDay).map { entities ->
            entities.map { it.toAgendaItem() }
        }
    }

    override suspend fun getItemById(id: String): AgendaItem? {
        return agendaItemDao.getById(id)?.toAgendaItem()
    }

    override suspend fun getAllPendingWithNotifications(): List<AgendaItem> {
        return agendaItemDao.getAllPendingWithNotifications().map { it.toAgendaItem() }
    }

    override suspend fun upsertItem(item: AgendaItem) {
        agendaItemDao.upsert(item.toEntity())
    }

    override suspend fun deleteItem(id: String) {
        agendaItemDao.delete(id)
    }

    override suspend fun toggleCompleted(id: String, isCompleted: Boolean, updatedAt: Long) {
        agendaItemDao.toggleCompleted(id, isCompleted, updatedAt)
    }
}
