package com.muzu.capyfocus.ui.screens.agenda

import com.muzu.capyfocus.domain.models.AgendaCategory
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.models.AgendaItemType
import com.muzu.capyfocus.domain.models.AgendaPriority
import com.muzu.capyfocus.domain.models.NotificationOffset
import com.muzu.capyfocus.domain.models.RecurrenceType
import java.time.LocalDate

sealed interface AgendaEvent {
    data class SelectDate(val date: LocalDate) : AgendaEvent
    data class ChangeViewMode(val viewMode: AgendaViewMode) : AgendaEvent
    data class NavigateDate(val delta: Int) : AgendaEvent
    data class ShowAddDialog(val initialDate: LocalDate? = null, val initialStartMinute: Int? = null) : AgendaEvent
    data class ShowEditDialog(val item: AgendaItem) : AgendaEvent
    data class ShowDetailDialog(val item: AgendaItem) : AgendaEvent
    data object DismissDialogs : AgendaEvent
    data class SaveAgendaItem(
        val title: String,
        val description: String,
        val type: AgendaItemType,
        val category: AgendaCategory,
        val priority: AgendaPriority,
        val date: LocalDate,
        val startMinute: Int,
        val endMinute: Int,
        val recurrence: RecurrenceType,
        val notificationOffset: NotificationOffset?
    ) : AgendaEvent
    data class DeleteAgendaItem(val id: String) : AgendaEvent
    data class ToggleItemCompletion(val id: String, val isCompleted: Boolean) : AgendaEvent
    data class DragMoveItem(val item: AgendaItem, val newStartMinute: Int) : AgendaEvent
}
