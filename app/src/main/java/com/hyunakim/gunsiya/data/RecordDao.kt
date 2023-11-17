package com.hyunakim.gunsiya.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record) // Create

    @Query("SELECT * FROM records WHERE userId = :userId")
    fun getRecordsByUserId(userId : Int): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE userId = :userId AND date = :date")
    fun getRecordsByUserIdAndDate(userId : Int, date: String): Flow<Record>

    @Update
    suspend fun update(record: Record) // Update

    @Delete
    suspend fun delete(record: Record) // Delete

    // 여러 사용자를 한 번에 가져오는 메소드도 추가할 수 있습니다.
    @Query("SELECT * FROM records")
    fun getAllRecords(): Flow<List<Record>>
}