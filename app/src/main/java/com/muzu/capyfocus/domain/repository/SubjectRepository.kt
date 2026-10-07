package com.muzu.capyfocus.domain.repository

import com.muzu.capyfocus.domain.models.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun observeActiveSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: String): Subject?
    suspend fun upsertSubject(subject: Subject)
    suspend fun deleteSubject(id: String, timestamp: Long)
}
