package com.muzu.capyfocus.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
    @Query("SELECT * FROM home_config WHERE id = 1")
    fun observeConfig(): Flow<HomeConfigEntity?>

    @Upsert
    suspend fun upsertConfig(config: HomeConfigEntity)
}
