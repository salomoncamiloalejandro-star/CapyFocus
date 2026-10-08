package com.muzu.capyfocus.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_config")
data class HomeConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val backgroundImagePath: String? = null
)
