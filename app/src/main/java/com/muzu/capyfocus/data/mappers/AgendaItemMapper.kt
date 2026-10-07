package com.muzu.capyfocus.data.mappers

import com.muzu.capyfocus.data.local.room.AgendaItemEntity
import com.muzu.capyfocus.domain.models.AgendaCategory
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.models.AgendaItemType
import com.muzu.capyfocus.domain.models.AgendaPriority
import com.muzu.capyfocus.domain.models.NotificationOffset
import com.muzu.capyfocus.domain.models.RecurrenceType

fun AgendaItemEntity.toAgendaItem(): AgendaItem {
    return AgendaItem(
        id = id,
        title = title,
        description = description,
        type = runCatching { AgendaItemType.valueOf(type) }.getOrDefault(AgendaItemType.TASK),
        category = runCatching { AgendaCategory.valueOf(category) }.getOrDefault(AgendaCategory.OTHER),
        priority = runCatching { AgendaPriority.valueOf(priority) }.getOrDefault(AgendaPriority.MEDIUM),
        dateEpochDay = dateEpochDay,
        startMinuteOfDay = startMinuteOfDay,
        endMinuteOfDay = endMinuteOfDay,
        isCompleted = isCompleted,
        recurrenceType = runCatching { RecurrenceType.valueOf(recurrenceType) }.getOrDefault(RecurrenceType.NONE),
        notificationOffset = notificationOffsetMinutes?.let { minutes ->
            NotificationOffset.entries.firstOrNull { it.minutesBefore == minutes }
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun AgendaItem.toEntity(): AgendaItemEntity {
    return AgendaItemEntity(
        id = id,
        title = title,
        description = description,
        type = type.name,
        category = category.name,
        priority = priority.name,
        dateEpochDay = dateEpochDay,
        startMinuteOfDay = startMinuteOfDay,
        endMinuteOfDay = endMinuteOfDay,
        isCompleted = isCompleted,
        recurrenceType = recurrenceType.name,
        notificationOffsetMinutes = notificationOffset?.minutesBefore,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
