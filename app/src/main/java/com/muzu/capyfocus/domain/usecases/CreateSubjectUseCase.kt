package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.core.time.TimeProvider
import com.muzu.capyfocus.domain.models.Subject
import com.muzu.capyfocus.domain.repository.SubjectRepository
import java.util.UUID
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        name: String,
        colorArgb: Int,
        iconKey: String,
        sortOrder: Int = 0,
    ): Result<Subject> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Subject name cannot be empty"))
        }

        val now = timeProvider.currentTimeMillis()
        val subject = Subject(
            id = UUID.randomUUID().toString(),
            name = trimmedName,
            colorArgb = colorArgb,
            iconKey = iconKey,
            sortOrder = sortOrder,
            createdAt = now,
            updatedAt = now,
            isDeleted = false,
        )

        repository.upsertSubject(subject)
        return Result.success(subject)
    }
}
