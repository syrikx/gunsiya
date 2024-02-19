package com.hyunakim.gunsiya.ui.result

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.GunsiyaApplication
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.MainViewModel
import com.hyunakim.gunsiya.ui.home.HomeViewModel
import com.hyunakim.gunsiya.ui.user.UserSelect
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ResultScreen(mainViewModel: MainViewModel) {
    Column {
        Upload(mainViewModel=mainViewModel
//            onUploadClick = onUploadClick()
        )
    }
}

@Composable
fun Upload(
//    onUploadClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    mainViewModel: MainViewModel
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val userList = homeUiState.userList
    val coroutineScope = rememberCoroutineScope()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val isAgreedPrivacyPolicy by mainViewModel.isAgreedPrivatyPolicy.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(text = "데이터센터로 접안 데이터를 전송합니다.")
//        Button(
//            onClick = {
//                coroutineScope.launch {
//                    viewModel.uploadUsers()
//                    saveLastUploadTime(System.currentTimeMillis())
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//                    enabled = hasChanges && isAgreedPrivacyPolicy && userList.isNotEmpty(),
//        ) {
//            Text(text = "데이터 업로드")
//        }
        DisplayImageFromUrl(userList = homeUiState.userList, viewModel= viewModel)
    }
    val uploadStatus by viewModel.uploadStatus.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uploadStatus) {
        uploadStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Log.d("uploadStatus", it)
            viewModel.resetUploadStatus() // 상태 초기화
        }
    }
}

fun formatTime(timeInMillis: Long): String {
    // SimpleDateFormat 등을 사용하여 시간을 원하는 형식으로 변환합니다.
    return if (timeInMillis == 0L) "아직 접안 데이터를 업로드 한 적이 없습니다."
    else "마지막 업로드는 " + SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timeInMillis)) + "에 했습니다."
}


fun saveLastUploadTime(time: Long) {
    GunsiyaApplication.sharedPreferences.edit().putLong("lastUploadTime", time).apply()
}

fun getLastUploadTime(): Long {
    return GunsiyaApplication.sharedPreferences.getLong("lastUploadTime", 0)
}

@Composable
fun DisplayImageFromUrl(userList : List<User>, viewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)) {
    UserSelect(userList)
    val currentUser = viewModel.currentUser.collectAsState()
    val currentCode = currentUser.value.hospitalCode + currentUser.value.patientCode
    var imageUrl = remember { mutableStateOf("") }
    var isButtonClicked = remember { mutableStateOf(false) }
    val lastUploadTime = remember { mutableStateOf(getLastUploadTime()) }
    val coroutineScope = rememberCoroutineScope()
    Text(text = "${formatTime(lastUploadTime.value)}")
    Column {
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.uploadUsers()
                    saveLastUploadTime(System.currentTimeMillis())
                }
                imageUrl.value = "https://playfi.site/api/getfile/${currentCode}.png"
                isButtonClicked.value = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "결과 확인")
        }

        if (isButtonClicked.value) {
            CoilImage(
//                imageModel = { imageUrl.value }, // loading a network image or local resource using an URL.
                imageModel = { imageUrl.value },
//                modifier = modifier,
                // shows an indicator while loading an image.
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                // shows an error text if fail to load an image.
                failure = {
                    Text(text = " 아직 결과 레포트가 작성되지 않았습니다.\n 레포트는 매주 월요일 업로드 됩니다.")
                })
//                imageOptions = ImageOptions(
//                    contentScale = ContentScale.Crop,
//                    alignment = Alignment.Center
//                )
        }
    }
}


