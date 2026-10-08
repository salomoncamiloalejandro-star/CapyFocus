package com.muzu.capyfocus.ui.screens.pomodoro

import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.Subject

data class PomodoroUiState(
    val config: PomodoroConfig = PomodoroConfig(),
    val sessionType: PomodoroSessionType = PomodoroSessionType.WORK,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val timeLeftSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val currentBlockNumber: Int = 1,
    val selectedCategory: PomodoroCategory = PomodoroCategory.STUDY,
    val selectedSubject: Subject? = null,
    val subjects: List<Subject> = emptyList(),
    val isActivitySelectionVisible: Boolean = false,
    val isSettingsVisible: Boolean = false,
    val isAddSubjectDialogVisible: Boolean = false,
    val needsCategoryConfirmation: Boolean = true
)
