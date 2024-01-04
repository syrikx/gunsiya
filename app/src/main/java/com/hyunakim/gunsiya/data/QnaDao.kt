package com.hyunakim.gunsiya.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QnaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qna: Qna) // 질문 생성

    @Query("SELECT * FROM qnas WHERE qnaId = :qnaId")
    fun getQna(qnaId: String): Flow<Qna> // 특정 질문 읽기

//    @Query("SELECT * FROM qnas WHERE patient = :patient")
//    fun getQnasByPatient(patient : String): List<Flow<Qna>>

    @Update
    suspend fun update(qna: Qna) // 질문 업데이트

    @Delete
    suspend fun delete(qna: Qna) // 질문 삭제

    // 모든 질문을 가져오는 메서드
    @Query("SELECT * FROM qnas")
    fun getAllQnas(): Flow<List<Qna>>
}
