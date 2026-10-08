package com.muzu.capyfocus.ui.screens.home

import com.muzu.capyfocus.domain.models.AgendaItem

data class HomeUiState(
    val backgroundImagePath: String? = null,
    val upcomingItems: List<AgendaItem> = emptyList(),
    val isLoading: Boolean = false
)
