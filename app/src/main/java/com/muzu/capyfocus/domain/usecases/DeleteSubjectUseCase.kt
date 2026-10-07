package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.core.time.TimeProvider
import com.muzu.capyfocus.domain.repository.SubjectRepository
import javax.inject.Inject

class DeleteSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(id: String) {
        val now = timeProvider.currentTimeMillis()
        repository.deleteSubject(id, now)
    }
}
