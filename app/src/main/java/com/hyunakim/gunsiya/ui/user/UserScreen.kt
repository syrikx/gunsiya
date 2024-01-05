package com.hyunakim.gunsiya.ui.user

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.AlarmManagerHelper
import com.hyunakim.gunsiya.MainViewModelFactory
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.MainViewModel
import com.hyunakim.gunsiya.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.regex.Pattern

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    viewModel: UserEntryViewModel = viewModel(factory= AppViewModelProvider.Factory),
) {
    val allUsersState by viewModel.allUsersState.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val alarmManagerHelper = remember { AlarmManagerHelper(context) }
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(alarmManagerHelper))
    val alarmTime by mainViewModel.alarmTime.collectAsState() // 현재 설정된 알림 시각


    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showTimePicker = true }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 16.dp) // Increase side padding
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "알림 설정")
                    Text(
                        text = if (alarmTime.isNotEmpty()) "$alarmTime" else "알림 없음",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) { innerPadding -> // 이 innerPadding을 활용하여 콘텐츠에 적절한 패딩을 적용
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding) // 여기에 innerPadding 적용
        ) {
            UserSelect(allUsersState.userList, isUserScreen = true)
            UserEntryScreen(
                navigateBack = { /*TODO*/ },
                onNavigateUp = { /*TODO*/ },
                viewModel = viewModel
            )
        }
    }
    // 시간 선택 다이얼로그 표시
    if (showTimePicker) {
        TimePickerExample(showTimePicker = showTimePicker, onTimeSelected = { hour, minute ->
            // 시간 선택 후 처리 로직
            showTimePicker = false
            // 예: ViewModel을 통해 알림 시간 설정
            mainViewModel.setAlarmTime(hour, minute)
        })
    }
}
@Composable
fun TimePickerExample(showTimePicker: Boolean, onTimeSelected: (Int, Int) -> Unit) {
    val context = LocalContext.current

    if (showTimePicker) {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = currentTime.hour
        val minute = currentTime.minute

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                onTimeSelected(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true // 24시간 형식 사용 여부
        ).show()
    }
}


//@Composable
//fun UserScreen(
//    modifier: Modifier = Modifier,
//    viewModel: UserEntryViewModel = viewModel(factory= AppViewModelProvider.Factory)
//) {
//    Column {
//        val userList: List<User> = listOf(
//            User(
//                id = 1,
//                name = "홍길동",
//                birthDate = "1990-01-01",
//                hospitalCode = "H001",
//                patientCode = "P001"
//            ),
//            User(
//                id = 2,
//                name = "이순신",
//                birthDate = "1980-12-31",
//                hospitalCode = "H002",
//                patientCode = "P002"
//            )
//        )
//        Text(text = "User")
//        UserSelect(userList)
//        UserInput(viewModel.userUiState)
//    }
//}

@Composable
fun UserSelect(
    userList : List<User>,
    isUserScreen : Boolean = false,
    viewModel: UserEntryViewModel = viewModel(factory= AppViewModelProvider.Factory),
    homeViewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)
){
    val coroutineScope = rememberCoroutineScope()
    val currentUser = homeViewModel.currentUser.collectAsState()
    Row {
        val context = LocalContext.current
        if (isUserScreen){
            Button(
                onClick = {
                    Toast.makeText(context, "새 사용자를 등록합니다.", Toast.LENGTH_SHORT).show()
                    viewModel.initUser()
                }) {
                Text("+")
            }
        }
        userList.forEach{ it ->
            Button(
                onClick = {
                    coroutineScope.launch {
//                        Log.d("get user", "${it.id}")
                        var user = viewModel.getUser(it.id)
//                        Log.d("user", "${user.userDetails.id}")
                        homeViewModel.updateCurrentUser(it)
                        viewModel.updateUserSelectedTime()
                        Log.d("userdetails", viewModel.userUiState.userDetails.lastSelectedTime.toString())
//                        Log.d("records", "${homeViewModel.getUserRecords(it.id)}")
                    }
//                    Toast.makeText(context, "${homeViewModel.currentUser.value.id}", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (it == currentUser.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    contentColor = if (it == currentUser.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
            ) {
                if (it == currentUser.value){
                    Text("${it.name} (선택)")
                } else {
                    Text(it.name)
                }
            }
        }
    }
}

@Composable
fun UserEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: UserEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
//    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        UserEntryBody(
            userUiState = viewModel.userUiState,
//            userUiState = uiState.value,
            onUserValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveUser()
                    navigateBack()
                }
            },
            onCancleClick = {
                    viewModel.initUser()
                    navigateBack()
            },
            onDeleteClick = {
                coroutineScope.launch {
                    viewModel.deleteUser(viewModel.userUiState.userDetails.toUser())
                    navigateBack()
                }
            },
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun UserEntryBody(
    userUiState: UserUiState,
    onUserValueChange: (UserDetails) -> Unit,
    onSaveClick: () -> Unit,
    onCancleClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
//        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        UserInput(
            userDetails = userUiState.userDetails,
            onValueChange = onUserValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onSaveClick,
                enabled = userUiState.isEntryValid,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (userUiState.isEntryValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "저장")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onCancleClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = "취소")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = "삭제")
            }
        }
    }
//        Button(
//            onClick = onCancleClick,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "취소")
//        }
//        Button(
//            onClick = onSaveClick,
//            enabled = userUiState.isEntryValid,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "저장")
//        }
//        Button(
//            onClick = onDeleteClick,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "삭제")
//        }
//    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInput(
    userDetails: UserDetails,
    modifier: Modifier = Modifier,
    onValueChange: (UserDetails) -> Unit = {},
    enabled: Boolean = true
){
    val birthDateFocusRequester = remember { FocusRequester() }
    val hospitalCodeFocusRequester = remember { FocusRequester() }
    val patientCodeFocusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.padding(16.dp)) {
        // 이름 입력
        OutlinedTextField(
            value = userDetails.name,
            onValueChange = { onValueChange(userDetails.copy(name = it)) },
            label = { Text("이름") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { birthDateFocusRequester.requestFocus() }),
            modifier = Modifier
                .fillMaxWidth(), // 여기에 fillMaxWidth 추가
//            textStyle = TextStyle(color = Color.Black),
            singleLine = true
        )
        Spacer(modifier=Modifier.height(16.dp))

        // 생년월일 입력 (YYYY-MM-DD 형식)
        OutlinedTextField(
            value = userDetails.birthDate,
            onValueChange = {
                if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                    onValueChange(userDetails.copy(birthDate = it))
                }
            },
            label={Text("생년월일")},

            placeholder={Text("생년월일(YYYYMMDD)")},
            modifier = Modifier
                .focusRequester(birthDateFocusRequester)
                .fillMaxWidth(), // 여기에 fillMaxWidth 추가
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { hospitalCodeFocusRequester.requestFocus() }),
        )
        Spacer(modifier=Modifier.height(16.dp))

        // 병원코드 입력 (두 자리 숫자)
        OutlinedTextField(
            value=userDetails.hospitalCode,
            onValueChange={
                if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                    onValueChange(userDetails.copy(hospitalCode = it))
                }
            },
            label={Text("병원코드")},
            placeholder={Text("병원코드(순천향대학교병원:00)")},
            modifier = Modifier
                .focusRequester(hospitalCodeFocusRequester)
                .fillMaxWidth(), // 여기에 fillMaxWidth 추가
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { patientCodeFocusRequester.requestFocus() }),
        )
        Spacer(modifier=Modifier.height(16.dp))

        // 환자코드 입력 (7자리 숫자)
        OutlinedTextField(
            value=userDetails.patientCode,
            onValueChange={
                if (it.length <= 7 && it.all { char -> char.isDigit() }) {
                    onValueChange(userDetails.copy(patientCode = it))
                }
            },
            label={Text("환자코드")},
            placeholder={Text("환자코드(순천향대학교병원:7자리숫자)")},
            modifier = Modifier
                .focusRequester(patientCodeFocusRequester)
                .fillMaxWidth(), // 여기에 fillMaxWidth 추가
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier=Modifier.height(24.dp))
    }
}