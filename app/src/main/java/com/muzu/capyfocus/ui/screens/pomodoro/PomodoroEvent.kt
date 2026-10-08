package com.muzu.capyfocus.ui.screens.pomodoro

import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.Subject

sealed interface PomodoroEvent {
    data object StartTimer : PomodoroEvent
    data object PauseTimer : PomodoroEvent
    data object ResumeTimer : PomodoroEvent
    data object ResetTimer : PomodoroEvent
    data object SkipTimer : PomodoroEvent
    data class SelectCategory(val category: PomodoroCategory) : PomodoroEvent
    data class SelectSubject(val subject: Subject?) : PomodoroEvent
    data object ConfirmActivity : PomodoroEvent
    data class ChangeSessionType(val type: PomodoroSessionType) : PomodoroEvent
    data class UpdateConfig(val config: PomodoroConfig) : PomodoroEvent
    data object ToggleSettingsDialog : PomodoroEvent
    data object ToggleActivitySelectionDialog : PomodoroEvent
    data object ToggleAddSubjectDialog : PomodoroEvent
    data class CreateSubject(val name: String) : PomodoroEvent
    data object Tick : PomodoroEvent
}
