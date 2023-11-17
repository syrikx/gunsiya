package com.hyunakim.gunsiya.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class OfflineRecordsRepository(private val recordDao: RecordDao) : RecordsRepository{
    override fun getAllRecordsStream(): Flow<List<Record>> = recordDao.getAllRecords()

    override fun getRecordsByUserId(userId: Int): Flow<List<Record>> = recordDao.getRecordsByUserId(userId)

    override fun getRecordsByUserIdAndDate(userId: Int, date: String): Flow<Record> = recordDao.getRecordsByUserIdAndDate(userId, date)

    override suspend fun insertRecord(user: Record) = recordDao.insert(user)

    override suspend fun deleteRecord(user: Record) = recordDao.delete(user)

    override suspend fun updateRecord(user: Record) = recordDao.update(user)
}