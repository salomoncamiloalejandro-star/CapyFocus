package com.muzu.capyfocus.domain.repository

import com.muzu.capyfocus.domain.models.AgendaItem
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    fun observeItemsUpToEndDate(endEpochDay: Long): Flow<List<AgendaItem>>
    suspend fun getItemById(id: String): AgendaItem?
    suspend fun getAllPendingWithNotifications(): List<AgendaItem>
    suspend fun upsertItem(item: AgendaItem)
    suspend fun deleteItem(id: String)
    suspend fun toggleCompleted(id: String, isCompleted: Boolean, updatedAt: Long)
}
