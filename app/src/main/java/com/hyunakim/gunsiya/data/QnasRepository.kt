package com.hyunakim.gunsiya.data

import kotlinx.coroutines.flow.Flow

interface QnasRepository {
    /**
     * 모든 Qna 데이터를 스트림으로 검색합니다.
     */
    fun getAllQnas(): Flow<List<Qna>>

    /**
     * 주어진 [id]와 일치하는 Qna 데이터를 스트림으로 검색합니다.
     */
    fun getQna(id: String): Flow<Qna?>
//    fun getQnaByPatient(patient: String): List<Flow<Qna?>>

    /**
     * 데이터 소스에 Qna를 삽입합니다.
     */
    suspend fun insertQna(qna: Qna)

    /**
     * 데이터 소스에서 Qna를 삭제합니다.
     */
    suspend fun deleteQna(qna: Qna)

    /**
     * 데이터 소스의 Qna를 업데이트합니다.
     */
    suspend fun updateQna(qna: Qna)

    // 서버에서 Qna 데이터를 가져와 로컬 데이터베이스를 업데이트하는 메서드
    suspend fun fetchQnasByPatientFromServer(patientId: String)
}

