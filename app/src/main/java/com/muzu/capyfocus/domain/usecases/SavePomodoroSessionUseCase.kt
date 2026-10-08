package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroSession
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.PomodoroStatus
import com.muzu.capyfocus.domain.repository.PomodoroRepository
import java.util.UUID
import javax.inject.Inject

class SavePomodoroSessionUseCase @Inject constructor(
    private val repository: PomodoroRepository
) {
    suspend operator fun invoke(
        category: PomodoroCategory,
        subjectId: String?,
        subjectName: String?,
        type: PomodoroSessionType,
        status: PomodoroStatus,
        durationMinutes: Int
    ) {
        val session = PomodoroSession(
            id = UUID.randomUUID().toString(),
            category = category,
            subjectId = subjectId,
            subjectName = subjectName,
            type = type,
            status = status,
            durationMinutes = durationMinutes,
            timestampEpochMilli = System.currentTimeMillis()
        )
        repository.insertSession(session)
    }
}
