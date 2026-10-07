package com.muzu.capyfocus.ui.screens.agenda

import com.muzu.capyfocus.domain.models.AgendaItem
import java.time.LocalDate

data class AgendaUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val viewMode: AgendaViewMode = AgendaViewMode.DAY,
    val items: List<AgendaItem> = emptyList(),
    val isLoading: Boolean = true,
    val isAddEditDialogVisible: Boolean = false,
    val editingItem: AgendaItem? = null,
    val isDetailVisible: Boolean = false,
    val detailItem: AgendaItem? = null,
    val dialogInitialStartMinute: Int = 540,
    val errorMessage: String? = null
)
