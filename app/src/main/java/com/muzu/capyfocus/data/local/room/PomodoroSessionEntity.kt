package com.muzu.capyfocus.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey
    val id: String,
    val category: String, // STUDY, WORK, EXERCISE, OTHER
    val subjectId: String?,
    val subjectName: String?,
    val type: String, // WORK, SHORT_BREAK, LONG_BREAK
    val status: String, // COMPLETED, SKIPPED
    val durationMinutes: Int,
    val timestampEpochMilli: Long
)
