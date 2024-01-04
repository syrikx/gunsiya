package com.hyunakim.gunsiya.ui.qna

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunakim.gunsiya.data.Qna
import com.hyunakim.gunsiya.data.QnasRepository
import com.hyunakim.gunsiya.data.apiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class QnaViewModel(
    private val qnasRepository: QnasRepository // Qna 데이터를 관리하는 레포지토리
) : ViewModel() {

    // Qna 목록을 관리하는 상태
//    private val _qnas = MutableStateFlow<List<Qna>>(emptyList())
//    val qnas: Flow<List<Qna>> = _qnas.asStateFlow()

    // Flow를 직접 노출합니다.
    val qnas: Flow<List<Qna>> = qnasRepository.getAllQnas()
        .map { qnasList ->
            qnasList.sortedWith(compareBy<Qna> { it.dateCreated }.thenBy { it.qnaId })
        }



    // 사용자의 질문을 서버에 전송합니다.
    fun submitQuestion(patient: String, question: String) {
        viewModelScope.launch {
            try {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                var newQna = Qna(
                    qnaId = "",
                    backendQnaId = "", // 고유 ID 생성 함수
                    patient = patient,
                    question = question,
                    answer = "",
                    isAnswered = false,
                    dateCreated = now,
                    dateAnswered = null
                )
                Log.d("qnaViewModel submitQuestion userId :", patient)
                val response = apiService.sendQna(newQna)
                if (response.success) {
                    Log.d("qnaViewModel", "QnA submitted successfully. ID: ${response.qnaId}")
                    newQna.qnaId = response.qnaId
                    val insertResult = qnasRepository.insertQna(newQna)
                    Log.d("qnaViewModel", "Inserted QnA ID: $insertResult")
                } else {
                    Log.e("qnaViewModel", "Failed to submit QnA")
                }
                Log.d("qnaViewModel on submit","fetchQnasFromServer fetch : ")
//                qnasRepository.fetchQnasFromServer()
//                fetchQnas()
//                getAllQnas() // 질문 추가 후 목록 갱신
            } catch (e: Exception) {
                Log.e("qnaViewModel submitQuestion error",e.toString())
                // 실패 처리 로직 (예: 에러 메시지 표시 등)
            }
        }
    }

//    fun fetchQnas() {
//        viewModelScope.launch {
//            Log.d("qnaViewModel","fetchQnasFromServer fetch : ")
//            qnasRepository.fetchQnasFromServer()
//            // 필요한 경우 UI 업데이트를 위한 추가적인 로직
//        }
//    }

    fun fetchQnasByPatientFromServer(patientId: String) {
        viewModelScope.launch {
            Log.d("fetchQnasByPatientFromServer at viewModel", patientId)
            qnasRepository.fetchQnasByPatientFromServer(patientId)
        }
    }

    // 모든 Qna를 조회하는 함수
//    fun getAllQnas() {
//        viewModelScope.launch {
//            _qnas.value = qnasRepository.getAllQnasStream().collectAsState()
//        }
//    }
}
