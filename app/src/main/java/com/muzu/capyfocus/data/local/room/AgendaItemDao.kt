package com.muzu.capyfocus.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaItemDao {

    @Query("SELECT * FROM agenda_items WHERE dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay ORDER BY startMinuteOfDay ASC")
    fun observeItemsForDateRange(startEpochDay: Long, endEpochDay: Long): Flow<List<AgendaItemEntity>>

    @Query("SELECT * FROM agenda_items WHERE id = :id")
    suspend fun getById(id: String): AgendaItemEntity?

    @Query("SELECT * FROM agenda_items WHERE isCompleted = 0 AND notificationOffsetMinutes IS NOT NULL")
    suspend fun getAllPendingWithNotifications(): List<AgendaItemEntity>

    @Upsert
    suspend fun upsert(item: AgendaItemEntity)

    @Query("DELETE FROM agenda_items WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE agenda_items SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :id")
    suspend fun toggleCompleted(id: String, isCompleted: Boolean, updatedAt: Long)
}
