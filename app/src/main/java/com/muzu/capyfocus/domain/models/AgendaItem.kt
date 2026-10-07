package com.muzu.capyfocus.domain.models

data class AgendaItem(
    val id: String,
    val title: String,
    val description: String,
    val type: AgendaItemType,
    val category: AgendaCategory,
    val priority: AgendaPriority,
    val dateEpochDay: Long,
    val startMinuteOfDay: Int,
    val endMinuteOfDay: Int,
    val isCompleted: Boolean,
    val recurrenceType: RecurrenceType,
    val notificationOffset: NotificationOffset?,
    val createdAt: Long,
    val updatedAt: Long
)
