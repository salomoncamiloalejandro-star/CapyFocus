package com.muzu.capyfocus.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agenda_items")
data class AgendaItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val type: String,
    val category: String,
    val priority: String,
    val dateEpochDay: Long,
    val startMinuteOfDay: Int,
    val endMinuteOfDay: Int,
    val isCompleted: Boolean,
    val recurrenceType: String,
    val notificationOffsetMinutes: Long?,
    val createdAt: Long,
    val updatedAt: Long
)
