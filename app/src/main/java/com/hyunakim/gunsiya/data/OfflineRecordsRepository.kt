package com.hyunakim.gunsiya.data

import kotlinx.coroutines.flow.Flow

class OfflineRecordsRepository(private val recordDao: RecordDao) : RecordsRepository{
    override fun getAllRecordsStream(): Flow<List<Record>> = recordDao.getAllRecords()

    override fun getRecordStream(id: Int): Flow<Record?> = recordDao.getRecord(id)

    override suspend fun insertRecord(user: Record) = recordDao.insert(user)

    override suspend fun deleteRecord(user: Record) = recordDao.delete(user)

    override suspend fun updateRecord(user: Record) = recordDao.update(user)
}