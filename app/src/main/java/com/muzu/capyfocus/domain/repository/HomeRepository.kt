package com.muzu.capyfocus.domain.repository

import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeBackgroundImagePath(): Flow<String?>
    suspend fun saveBackgroundImagePath(path: String?)
}
