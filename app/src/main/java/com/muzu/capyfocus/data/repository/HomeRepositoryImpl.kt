package com.muzu.capyfocus.data.repository

import com.muzu.capyfocus.data.local.room.HomeConfigEntity
import com.muzu.capyfocus.data.local.room.HomeDao
import com.muzu.capyfocus.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeDao: HomeDao
) : HomeRepository {
    override fun observeBackgroundImagePath(): Flow<String?> {
        return homeDao.observeConfig().map { it?.backgroundImagePath }
    }

    override suspend fun saveBackgroundImagePath(path: String?) {
        homeDao.upsertConfig(HomeConfigEntity(id = 1, backgroundImagePath = path))
    }
}
