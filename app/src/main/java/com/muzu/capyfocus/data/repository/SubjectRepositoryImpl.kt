package com.muzu.capyfocus.data.repository

import com.muzu.capyfocus.data.local.room.SubjectDao
import com.muzu.capyfocus.data.mappers.toEntity
import com.muzu.capyfocus.data.mappers.toSubject
import com.muzu.capyfocus.domain.models.Subject
import com.muzu.capyfocus.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
) : SubjectRepository {

    override fun observeActiveSubjects(): Flow<List<Subject>> {
        return subjectDao.observeActive().map { entities ->
            entities.map { it.toSubject() }
        }
    }

    override suspend fun getSubjectById(id: String): Subject? {
        return subjectDao.getById(id)?.toSubject()
    }

    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsert(subject.toEntity())
    }

    override suspend fun deleteSubject(id: String, timestamp: Long) {
        subjectDao.softDelete(id, timestamp)
    }
}
