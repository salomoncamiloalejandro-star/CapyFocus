package com.muzu.capyfocus.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.PomodoroStatus
import com.muzu.capyfocus.domain.usecases.ObservePomodoroSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val observePomodoroSessionsUseCase: ObservePomodoroSessionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observePomodoroSessionsUseCase().collect { sessions ->
                val completed = sessions.filter { it.type == PomodoroSessionType.WORK && it.status == PomodoroStatus.COMPLETED }
                val skipped = sessions.filter { it.type == PomodoroSessionType.WORK && it.status == PomodoroStatus.SKIPPED }
                val workMins = completed.sumOf { it.durationMinutes }
                val breakMins = sessions.filter { it.type != PomodoroSessionType.WORK && it.status == PomodoroStatus.COMPLETED }.sumOf { it.durationMinutes }
                val catCounts = completed.groupBy { it.category }.mapValues { entry -> entry.value.size }
                val subjCounts = completed.filter { it.subjectName != null }.groupBy { it.subjectName!! }.mapValues { entry -> entry.value.size }

                _uiState.update {
                    it.copy(
                        sessions = sessions,
                        completedBlocksCount = completed.size,
                        skippedBlocksCount = skipped.size,
                        totalWorkMinutes = workMins,
                        totalBreakMinutes = breakMins,
                        categoryCounts = catCounts,
                        subjectCounts = subjCounts
                    )
                }
            }
        }
    }
}
