package com.muzu.capyfocus.data.mappers

import com.muzu.capyfocus.data.local.room.SubjectEntity
import com.muzu.capyfocus.domain.models.Subject

fun SubjectEntity.toSubject(): Subject {
    return Subject(
        id = id,
        name = name,
        colorArgb = colorArgb,
        iconKey = iconKey,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )
}

fun Subject.toEntity(): SubjectEntity {
    return SubjectEntity(
        id = id,
        name = name,
        colorArgb = colorArgb,
        iconKey = iconKey,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )
}
