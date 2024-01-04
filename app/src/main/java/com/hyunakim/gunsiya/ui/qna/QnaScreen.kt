package com.hyunakim.gunsiya.ui.qna

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.data.Qna
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.home.HomeViewModel
import kotlinx.datetime.LocalDateTime

@Composable
fun QnaScreen(viewModel: QnaViewModel = viewModel(factory= AppViewModelProvider.Factory), homeViewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)) {
    var questionText by remember { mutableStateOf("") }
    val currentUser = homeViewModel.currentUser.collectAsState()
    var userId = currentUser.value.hospitalCode + currentUser.value.patientCode
    val qnas by viewModel.qnas.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()

    LaunchedEffect(qnas, userId) {
        if (qnas.isNotEmpty()) {
            listState.animateScrollToItem(qnas.lastIndex) // 마지막 인덱스로 스크롤
        }
        Log.d("LaunchedEffect userId", userId)
        viewModel.fetchQnasByPatientFromServer(userId)
    }

//    Column(modifier = Modifier.padding(16.dp)) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "상담실", style = MaterialTheme.typography.headlineMedium)

        // 질문을 입력하는 텍스트 필드
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("질문을 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // 질문을 전송하는 버튼
        Button(
            onClick = {
                viewModel.submitQuestion(userId, questionText)
                viewModel.fetchQnasByPatientFromServer(userId)
                questionText = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("질문 전송")
        }

        // 답변 확인 버튼
        Button(
            onClick = { viewModel.fetchQnasByPatientFromServer(userId) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("답변 확인")
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(qnas.size) { index ->
                QnaBubble(qna = qnas[index])
            }
        }
//        Column(modifier = Modifier.padding(16.dp)) {
//            // QnA 목록을 표시
//            qnas.forEach { qna ->
//                QnaBubble(qna = qna)
//            }
//        }
    }
}

@Composable
fun QnaBubble(qna: Qna) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        // 질문 말풍선
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(text = "Q")
            BubbleContent(message = qna.question, alignRight = false)
        }
        // 질문 날짜 표시
        qna.dateCreated?.let {
            Text(
                text = formatDate(it),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        // 답변 말풍선
        if (qna.answer.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                BubbleContent(message = qna.answer, alignRight = true)
            }
            // 답변 날짜 표시
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                qna.dateAnswered?.let {
                    Text(
                        text = formatDate(it),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

// 날짜 포맷 함수
fun formatDate(date: LocalDateTime): String {
    return "${date.monthNumber}월 ${date.dayOfMonth}일 ${date.hour}:${date.minute}"
}

@Composable
fun IconBubble(text: String) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        modifier = Modifier.size(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center) // 중앙 정렬
            )
        }
    }
}

@Composable
fun BubbleContent(message: String, alignRight: Boolean) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (alignRight) Color(0xFFE1F5FE) else Color(0xFFF0F0F0)),
        modifier = Modifier
            .widthIn(max = 300.dp) // 최대 너비 설정
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
