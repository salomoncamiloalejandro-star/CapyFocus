package com.muzu.capyfocus.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorArgb: Int,
    val iconKey: String,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)