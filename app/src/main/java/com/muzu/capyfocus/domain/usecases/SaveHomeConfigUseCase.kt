package com.muzu.capyfocus.domain.usecases

import com.muzu.capyfocus.domain.repository.HomeRepository
import javax.inject.Inject

class SaveHomeConfigUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(path: String?) {
        repository.saveBackgroundImagePath(path)
    }
}
