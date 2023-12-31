package com.hyunakim.gunsiya.data

import kotlinx.coroutines.flow.Flow

interface RecordsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllRecordsStream(): Flow<List<Record>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getRecordsByUserId(userId : Int): Flow<List<Record>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */

    fun getRecordsByUserIdAndDate(userId : Int, date : String): Flow<Record>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */

    /**
     * Insert item in the data source
     */
    suspend fun insertRecord(item: Record)

    /**
     * Delete item from the data source
     */
    suspend fun deleteRecord(item: Record)

    /**
     * Update item in the data source
     */
    suspend fun updateRecord(item: Record)
}