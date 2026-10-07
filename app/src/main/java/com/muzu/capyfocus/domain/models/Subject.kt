package com.muzu.capyfocus.domain.models

data class Subject(
    val id: String,
    val name: String,
    val colorArgb: Int,
    val iconKey: String,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)