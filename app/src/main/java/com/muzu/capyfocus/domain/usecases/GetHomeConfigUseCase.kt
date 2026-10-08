package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHomeConfigUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.observeBackgroundImagePath()
    }
}
