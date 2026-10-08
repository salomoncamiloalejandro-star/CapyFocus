package com.muzu.capyfocus.domain.models

enum class PomodoroCategory(val label: String) {
    STUDY("Estudiar"),
    WORK("Trabajar"),
    EXERCISE("Entrenar"),
    OTHER("Otro")
}

enum class PomodoroSessionType {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}

enum class PomodoroStatus {
    COMPLETED,
    SKIPPED
}

data class PomodoroSession(
    val id: String,
    val category: PomodoroCategory,
    val subjectId: String?,
    val subjectName: String?,
    val type: PomodoroSessionType,
    val status: PomodoroStatus,
    val durationMinutes: Int,
    val timestampEpochMilli: Long
)

data class PomodoroConfig(
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val blocksBeforeLongBreak: Int = 4,
    val autoStart: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
