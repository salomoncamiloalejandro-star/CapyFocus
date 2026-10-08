package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.models.RecurrenceType
import com.muzu.capyfocus.domain.repository.AgendaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class ObserveAgendaForDateRangeUseCase @Inject constructor(
    private val repository: AgendaRepository,
) {
    operator fun invoke(startEpochDay: Long, endEpochDay: Long): Flow<List<AgendaItem>> {
        return repository.observeItemsUpToEndDate(endEpochDay).map { items ->
            val result = mutableListOf<AgendaItem>()
            for (item in items) {
                val baseDate = LocalDate.ofEpochDay(item.dateEpochDay)
                if (item.recurrenceType == RecurrenceType.NONE) {
                    if (item.dateEpochDay in startEpochDay..endEpochDay) {
                        result.add(item)
                    }
                } else {
                    for (epochDay in startEpochDay..endEpochDay) {
                        val checkDate = LocalDate.ofEpochDay(epochDay)
                        if (isDateMatchingRecurrence(checkDate, baseDate, item.recurrenceType)) {
                            val instanceId = if (epochDay == item.dateEpochDay) item.id else "${item.id}_$epochDay"
                            result.add(item.copy(id = instanceId, dateEpochDay = epochDay))
                        }
                    }
                }
            }
            result.sortedBy { it.startMinuteOfDay }
        }
    }

    private fun isDateMatchingRecurrence(
        date: LocalDate,
        baseDate: LocalDate,
        recurrenceType: RecurrenceType,
    ): Boolean {
        if (date.isBefore(baseDate)) return false

        return when (recurrenceType) {
            RecurrenceType.NONE -> date == baseDate
            RecurrenceType.DAILY -> true
            RecurrenceType.WEEKDAYS -> date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
            RecurrenceType.WEEKLY -> date.dayOfWeek == baseDate.dayOfWeek
            RecurrenceType.MONTHLY -> date.dayOfMonth == baseDate.dayOfMonth ||
                (date.dayOfMonth == date.lengthOfMonth() && baseDate.dayOfMonth > date.lengthOfMonth())
            RecurrenceType.CUSTOM -> date.dayOfWeek == baseDate.dayOfWeek
        }
    }
}
