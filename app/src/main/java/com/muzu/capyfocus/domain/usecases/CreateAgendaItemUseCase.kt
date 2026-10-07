package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.core.time.TimeProvider
import com.muzu.capyfocus.domain.models.AgendaCategory
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.models.AgendaItemType
import com.muzu.capyfocus.domain.models.AgendaPriority
import com.muzu.capyfocus.domain.models.NotificationOffset
import com.muzu.capyfocus.domain.models.RecurrenceType
import com.muzu.capyfocus.domain.repository.AgendaRepository
import java.util.UUID
import javax.inject.Inject

class CreateAgendaItemUseCase @Inject constructor(
    private val repository: AgendaRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        type: AgendaItemType,
        category: AgendaCategory,
        priority: AgendaPriority,
        dateEpochDay: Long,
        startMinuteOfDay: Int,
        endMinuteOfDay: Int,
        recurrenceType: RecurrenceType,
        notificationOffset: NotificationOffset?,
    ): Result<AgendaItem> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("El título no puede estar vacío"))
        }

        val now = timeProvider.currentTimeMillis()
        val item = AgendaItem(
            id = UUID.randomUUID().toString(),
            title = trimmedTitle,
            description = description.trim(),
            type = type,
            category = category,
            priority = priority,
            dateEpochDay = dateEpochDay,
            startMinuteOfDay = startMinuteOfDay,
            endMinuteOfDay = endMinuteOfDay,
            isCompleted = false,
            recurrenceType = recurrenceType,
            notificationOffset = notificationOffset,
            createdAt = now,
            updatedAt = now,
        )

        repository.upsertItem(item)
        return Result.success(item)
    }
}
