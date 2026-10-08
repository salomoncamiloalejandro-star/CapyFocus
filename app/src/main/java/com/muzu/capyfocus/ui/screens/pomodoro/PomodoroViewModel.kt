package com.muzu.capyfocus.ui.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzu.capyfocus.core.pomodoro.PomodoroNotificationHelper
import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.PomodoroStatus
import com.muzu.capyfocus.domain.usecases.CreateSubjectUseCase
import com.muzu.capyfocus.domain.usecases.ObservePomodoroConfigUseCase
import com.muzu.capyfocus.domain.usecases.ObserveSubjectsUseCase
import com.muzu.capyfocus.domain.usecases.SavePomodoroSessionUseCase
import com.muzu.capyfocus.domain.usecases.UpdatePomodoroConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val observePomodoroConfigUseCase: ObservePomodoroConfigUseCase,
    private val updatePomodoroConfigUseCase: UpdatePomodoroConfigUseCase,
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase,
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val notificationHelper: PomodoroNotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var targetTimestamp: Long = 0L

    init {
        viewModelScope.launch {
            observePomodoroConfigUseCase().collect { config ->
                _uiState.update { state ->
                    val defaultTime = config.workDurationMinutes * 60
                    if (!state.isRunning && !state.isPaused) {
                        state.copy(config = config, timeLeftSeconds = defaultTime, totalSeconds = defaultTime)
                    } else {
                        state.copy(config = config)
                    }
                }
            }
        }

        viewModelScope.launch {
            observeSubjectsUseCase().collect { subjects ->
                _uiState.update { it.copy(subjects = subjects) }
            }
        }
    }

    fun onEvent(event: PomodoroEvent) {
        when (event) {
            PomodoroEvent.StartTimer -> {
                val state = _uiState.value
                if (state.sessionType == PomodoroSessionType.WORK && state.needsCategoryConfirmation) {
                    _uiState.update { it.copy(isActivitySelectionVisible = true) }
                } else {
                    startRunning()
                }
            }
            PomodoroEvent.ConfirmActivity -> {
                val state = _uiState.value
                if (state.selectedCategory == PomodoroCategory.STUDY && state.selectedSubject == null && state.subjects.isNotEmpty()) {
                    return
                }
                _uiState.update { it.copy(isActivitySelectionVisible = false, needsCategoryConfirmation = false) }
                startRunning()
            }
            PomodoroEvent.PauseTimer -> pauseTimer()
            PomodoroEvent.ResumeTimer -> resumeTimer()
            PomodoroEvent.ResetTimer -> {
                resetTimer()
                _uiState.update { it.copy(needsCategoryConfirmation = true) }
            }
            PomodoroEvent.SkipTimer -> skipTimer()
            is PomodoroEvent.SelectCategory -> {
                _uiState.update { it.copy(selectedCategory = event.category, selectedSubject = null) }
            }
            is PomodoroEvent.SelectSubject -> {
                _uiState.update { it.copy(selectedSubject = event.subject) }
            }
            is PomodoroEvent.ChangeSessionType -> changeSessionType(event.type)
            is PomodoroEvent.UpdateConfig -> {
                viewModelScope.launch {
                    updatePomodoroConfigUseCase(event.config)
                }
            }
            PomodoroEvent.ToggleSettingsDialog -> {
                _uiState.update { it.copy(isSettingsVisible = !it.isSettingsVisible) }
            }
            PomodoroEvent.ToggleActivitySelectionDialog -> {
                _uiState.update { it.copy(isActivitySelectionVisible = !it.isActivitySelectionVisible) }
            }
            PomodoroEvent.ToggleAddSubjectDialog -> {
                _uiState.update { it.copy(isAddSubjectDialogVisible = !it.isAddSubjectDialogVisible) }
            }
            is PomodoroEvent.CreateSubject -> {
                viewModelScope.launch {
                    val result = createSubjectUseCase(
                        name = event.name,
                        colorArgb = 0xFF4CAF50.toInt(),
                        iconKey = "book"
                    )
                    result.onSuccess { subject ->
                        _uiState.update { it.copy(selectedSubject = subject, isAddSubjectDialogVisible = false, isActivitySelectionVisible = false, needsCategoryConfirmation = false) }
                        startRunning()
                    }
                }
            }
            PomodoroEvent.Tick -> handleTick()
        }
    }

    private fun startRunning() {
        val state = _uiState.value
        val duration = if (state.isPaused) state.timeLeftSeconds else when (state.sessionType) {
            PomodoroSessionType.WORK -> state.config.workDurationMinutes * 60
            PomodoroSessionType.SHORT_BREAK -> state.config.shortBreakDurationMinutes * 60
            PomodoroSessionType.LONG_BREAK -> state.config.longBreakDurationMinutes * 60
        }

        targetTimestamp = System.currentTimeMillis() + (duration * 1000L)
        _uiState.update {
            it.copy(
                isRunning = true,
                isPaused = false,
                timeLeftSeconds = duration,
                totalSeconds = duration,
                isActivitySelectionVisible = false
            )
        }

        startTimerJob()
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val remaining = ((targetTimestamp - System.currentTimeMillis()) / 1000L).toInt()
                if (remaining <= 0) {
                    _uiState.update { it.copy(timeLeftSeconds = 0) }
                    onBlockFinished()
                    break
                } else {
                    _uiState.update { it.copy(timeLeftSeconds = remaining) }
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        val remaining = ((targetTimestamp - System.currentTimeMillis()) / 1000L).toInt().coerceAtLeast(0)
        _uiState.update { it.copy(isRunning = false, isPaused = true, timeLeftSeconds = remaining) }
    }

    private fun resumeTimer() {
        val state = _uiState.value
        targetTimestamp = System.currentTimeMillis() + (state.timeLeftSeconds * 1000L)
        _uiState.update { it.copy(isRunning = true, isPaused = false) }
        startTimerJob()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        val state = _uiState.value
        val duration = when (state.sessionType) {
            PomodoroSessionType.WORK -> state.config.workDurationMinutes * 60
            PomodoroSessionType.SHORT_BREAK -> state.config.shortBreakDurationMinutes * 60
            PomodoroSessionType.LONG_BREAK -> state.config.longBreakDurationMinutes * 60
        }
        _uiState.update { it.copy(isRunning = false, isPaused = false, timeLeftSeconds = duration, totalSeconds = duration) }
    }

    private fun skipTimer() {
        timerJob?.cancel()
        val state = _uiState.value
        viewModelScope.launch {
            if (state.sessionType == PomodoroSessionType.WORK) {
                savePomodoroSessionUseCase(
                    category = state.selectedCategory,
                    subjectId = state.selectedSubject?.id,
                    subjectName = state.selectedSubject?.name,
                    type = PomodoroSessionType.WORK,
                    status = PomodoroStatus.SKIPPED,
                    durationMinutes = state.totalSeconds / 60
                )
            }
            moveToNextBlock()
        }
    }

    private fun onBlockFinished() {
        timerJob?.cancel()
        val state = _uiState.value
        val config = state.config

        viewModelScope.launch {
            if (state.sessionType == PomodoroSessionType.WORK) {
                savePomodoroSessionUseCase(
                    category = state.selectedCategory,
                    subjectId = state.selectedSubject?.id,
                    subjectName = state.selectedSubject?.name,
                    type = PomodoroSessionType.WORK,
                    status = PomodoroStatus.COMPLETED,
                    durationMinutes = state.totalSeconds / 60
                )
                notificationHelper.showInstantNotification("¡Pomodoro Finalizado!", "Bloque de trabajo completado con éxito.", config.soundEnabled, config.vibrationEnabled)
            } else {
                notificationHelper.showInstantNotification("Descanso Finalizado", "Es hora de volver al trabajo.", config.soundEnabled, config.vibrationEnabled)
            }
            moveToNextBlock()
            if (state.config.autoStart) {
                _uiState.update { it.copy(needsCategoryConfirmation = false) }
                startRunning()
            } else {
                _uiState.update { it.copy(needsCategoryConfirmation = true) }
            }
        }
    }

    private fun moveToNextBlock() {
        val state = _uiState.value
        val config = state.config

        when (state.sessionType) {
            PomodoroSessionType.WORK -> {
                if (state.currentBlockNumber >= config.blocksBeforeLongBreak) {
                    val duration = config.longBreakDurationMinutes * 60
                    _uiState.update {
                        it.copy(
                            sessionType = PomodoroSessionType.LONG_BREAK,
                            isRunning = false,
                            isPaused = false,
                            timeLeftSeconds = duration,
                            totalSeconds = duration
                        )
                    }
                } else {
                    val duration = config.shortBreakDurationMinutes * 60
                    _uiState.update {
                        it.copy(
                            sessionType = PomodoroSessionType.SHORT_BREAK,
                            isRunning = false,
                            isPaused = false,
                            timeLeftSeconds = duration,
                            totalSeconds = duration
                        )
                    }
                }
            }
            PomodoroSessionType.SHORT_BREAK -> {
                val nextBlock = state.currentBlockNumber + 1
                val duration = config.workDurationMinutes * 60
                _uiState.update {
                    it.copy(
                        sessionType = PomodoroSessionType.WORK,
                        currentBlockNumber = nextBlock,
                        isRunning = false,
                        isPaused = false,
                        timeLeftSeconds = duration,
                        totalSeconds = duration
                    )
                }
            }
            PomodoroSessionType.LONG_BREAK -> {
                val duration = config.workDurationMinutes * 60
                _uiState.update {
                    it.copy(
                        sessionType = PomodoroSessionType.WORK,
                        currentBlockNumber = 1,
                        isRunning = false,
                        isPaused = false,
                        timeLeftSeconds = duration,
                        totalSeconds = duration
                    )
                }
            }
        }
    }

    private fun changeSessionType(type: PomodoroSessionType) {
        timerJob?.cancel()
        val state = _uiState.value
        val duration = when (type) {
            PomodoroSessionType.WORK -> state.config.workDurationMinutes * 60
            PomodoroSessionType.SHORT_BREAK -> state.config.shortBreakDurationMinutes * 60
            PomodoroSessionType.LONG_BREAK -> state.config.longBreakDurationMinutes * 60
        }
        _uiState.update {
            it.copy(
                sessionType = type,
                isRunning = false,
                isPaused = false,
                timeLeftSeconds = duration,
                totalSeconds = duration,
                needsCategoryConfirmation = true
            )
        }
    }

    private fun handleTick() {
        // Handled in timerJob
    }
}
