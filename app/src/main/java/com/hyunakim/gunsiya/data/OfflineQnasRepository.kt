package com.hyunakim.gunsiya.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

class OfflineQnasRepository(private val qnaDao: QnaDao) : QnasRepository {
    override fun getAllQnas(): Flow<List<Qna>> = qnaDao.getAllQnas()

    override fun getQna(id: String): Flow<Qna?> = qnaDao.getQna(id)
//    override fun getQnaByPatient(patient: String): List<Flow<Qna?>> = qnaDao.getQnasByPatient(patient)
    override suspend fun insertQna(qna: Qna) = qnaDao.insert(qna)

    override suspend fun deleteQna(qna: Qna) = qnaDao.delete(qna)

    override suspend fun updateQna(qna: Qna) = qnaDao.update(qna)

    override suspend fun fetchQnasByPatientFromServer(patientId: String) {
        try {
            val response = apiService.getQnasByPatient(patientId)
            Log.d("offlineQnasRepository:", response.toString())
            response.forEach { qnaServer ->
                Log.d("offlineQnasRepository: insert ", qnaServer.toString())
                qnaDao.insert(convertQnaServerToQna(qnaServer))
            }
        } catch (e: Exception) {
            Log.e("offlineQnasRepository:", e.toString())
            // 에러 처리
        }
    }

    fun convertQnaServerToQna(qnaServer: QnaServer): Qna {
        // ISO-8601 포맷을 사용하여 문자열 날짜를 LocalDateTime으로 변환하는 함수
        fun parseDateTime(dateTimeString: String?): LocalDateTime? {
            return try {
                dateTimeString?.let { kotlinx.datetime.Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault()) }
            } catch (e: Exception) {
                null // 날짜 파싱에 실패한 경우 null 반환
            }
        }

        return Qna(
            qnaId = qnaServer.qnaId,
            backendQnaId = qnaServer.qnaId,
            patient = qnaServer.patient,
            question = qnaServer.question,
            answer = qnaServer.answer,
            isAnswered = qnaServer.isAnswered,
            dateCreated = parseDateTime(qnaServer.dateCreated),
            dateAnswered = parseDateTime(qnaServer.dateAnswered)
        )
    }

//    override suspend fun fetchQnasFromServer() {
//        try {
//            val qnasFromServer = apiService.getQnas()
//            Log.d("fetchQnasFromServer trying : ", qnasFromServer.toString())
//            // 서버에서 받은 데이터로 로컬 데이터베이스의 qna 테이블을 업데이트합니다.
//            qnasFromServer.forEach { qnaDao.insert(it) }
//        } catch (e: Exception) {
//            Log.e("fetchQnasFromServer error : ", e.toString())
//            // 에러 처리...
//        }
//    }
}
