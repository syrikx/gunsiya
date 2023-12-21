package com.hyunakim.gunsiya.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class OfflineQnasRepository(private val qnaDao: QnaDao) : QnasRepository {
    override fun getAllQnasStream(): Flow<List<Qna>> = qnaDao.getAllQnas()

    override fun getQnaStream(id: String): Flow<Qna?> = qnaDao.getQna(id)

    override suspend fun insertQna(qna: Qna) = qnaDao.insert(qna)

    override suspend fun deleteQna(qna: Qna) = qnaDao.delete(qna)

    override suspend fun updateQna(qna: Qna) = qnaDao.update(qna)

    override suspend fun fetchQnasFromServer() {
        try {
            val qnasFromServer = apiService.getQnas()
            Log.d("fetchQnasFromServer trying : ", qnasFromServer.toString())
            // 서버에서 받은 데이터로 로컬 데이터베이스의 qna 테이블을 업데이트합니다.
            qnasFromServer.forEach { qnaDao.insert(it) }
        } catch (e: Exception) {
            Log.e("fetchQnasFromServer error : ", e.toString())
            // 에러 처리...
        }
    }
}
