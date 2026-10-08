package com.muzu.capyfocus.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_config")
data class PomodoroConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val workDurationMinutes: Int = 25,
    val shortBreakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val blocksBeforeLongBreak: Int = 4,
    val autoStart: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
