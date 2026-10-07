package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.models.Subject
import com.muzu.capyfocus.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSubjectsUseCase @Inject constructor(
    private val repository: SubjectRepository,
) {
    operator fun invoke(): Flow<List<Subject>> {
        return repository.observeActiveSubjects()
    }
}
