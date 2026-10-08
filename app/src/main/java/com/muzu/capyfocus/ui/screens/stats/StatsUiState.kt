package com.muzu.capyfocus.ui.screens.stats

import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroSession

data class StatsUiState(
    val isLoading: Boolean = false,
    val sessions: List<PomodoroSession> = emptyList(),
    val completedBlocksCount: Int = 0,
    val skippedBlocksCount: Int = 0,
    val totalWorkMinutes: Int = 0,
    val totalBreakMinutes: Int = 0,
    val categoryCounts: Map<PomodoroCategory, Int> = emptyMap(),
    val subjectCounts: Map<String, Int> = emptyMap()
)
