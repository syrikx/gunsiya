package com.hyunakim.gunsiya.ui.qna

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.home.HomeViewModel

@Composable
fun QnaScreen(viewModel: QnaViewModel = viewModel(factory= AppViewModelProvider.Factory), homeViewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)) {
    var questionText by remember { mutableStateOf("") }
    val currentUser = homeViewModel.currentUser.collectAsState()
    var userId = currentUser.value.hospitalCode + currentUser.value.patientCode
    val context = LocalContext.current

    // viewModel에서 qnas Flow를 State로 변환
    val qnas by viewModel.qnas.collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "QnA")

        // 질문을 입력하는 텍스트 필드
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("질문을 입력하세요") },
            modifier = Modifier.padding(top = 8.dp)
        )

        // 질문을 전송하는 버튼
        Button(
            onClick = {
                viewModel.submitQuestion(userId, questionText)
                questionText = "" // 질문 전송 후 텍스트 필드 초기화
//                viewModel.fetchQnas()
//                Toast.makeText(context, "${qnas.toString()}", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("질문 전송")
//            Log.d("qnas : ", qnas.toString())
        }

        // Qna 목록을 표시
        qnas.forEach { qna ->
            Text("질문: ${qna.question}\n답변: ${qna.answer}", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
