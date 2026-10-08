package com.muzu.capyfocus.data.mappers

import com.muzu.capyfocus.data.local.room.PomodoroConfigEntity
import com.muzu.capyfocus.data.local.room.PomodoroSessionEntity
import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.models.PomodoroSession
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.PomodoroStatus

fun PomodoroSessionEntity.toDomain(): PomodoroSession {
    return PomodoroSession(
        id = id,
        category = try { PomodoroCategory.valueOf(category) } catch (_: Exception) { PomodoroCategory.OTHER },
        subjectId = subjectId,
        subjectName = subjectName,
        type = try { PomodoroSessionType.valueOf(type) } catch (_: Exception) { PomodoroSessionType.WORK },
        status = try { PomodoroStatus.valueOf(status) } catch (_: Exception) { PomodoroStatus.COMPLETED },
        durationMinutes = durationMinutes,
        timestampEpochMilli = timestampEpochMilli
    )
}

fun PomodoroSession.toEntity(): PomodoroSessionEntity {
    return PomodoroSessionEntity(
        id = id,
        category = category.name,
        subjectId = subjectId,
        subjectName = subjectName,
        type = type.name,
        status = status.name,
        durationMinutes = durationMinutes,
        timestampEpochMilli = timestampEpochMilli
    )
}

fun PomodoroConfigEntity.toDomain(): PomodoroConfig {
    return PomodoroConfig(
        workDurationMinutes = workDurationMinutes,
        shortBreakDurationMinutes = shortBreakDurationMinutes,
        longBreakDurationMinutes = longBreakDurationMinutes,
        blocksBeforeLongBreak = blocksBeforeLongBreak,
        autoStart = autoStart,
        soundEnabled = soundEnabled,
        vibrationEnabled = vibrationEnabled
    )
}

fun PomodoroConfig.toEntity(): PomodoroConfigEntity {
    return PomodoroConfigEntity(
        id = 1,
        workDurationMinutes = workDurationMinutes,
        shortBreakDurationMinutes = shortBreakDurationMinutes,
        longBreakDurationMinutes = longBreakDurationMinutes,
        blocksBeforeLongBreak = blocksBeforeLongBreak,
        autoStart = autoStart,
        soundEnabled = soundEnabled,
        vibrationEnabled = vibrationEnabled
    )
}
