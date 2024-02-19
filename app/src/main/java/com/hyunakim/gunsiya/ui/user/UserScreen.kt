package com.hyunakim.gunsiya.ui.user

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.LocalAlarmManagerHelper
import com.hyunakim.gunsiya.MainViewModelFactory
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.MainViewModel
import com.hyunakim.gunsiya.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    viewModel: UserEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    mainViewModel: MainViewModel,
) {
    val allUsersState by viewModel.allUsersState.collectAsState()
    var showTimePicker = remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val alarmManagerHelper = remember { AlarmManagerHelper(context) }
    val alarmManagerHelper = LocalAlarmManagerHelper.current
//    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(alarmManagerHelper))
    val alarmTime by mainViewModel.alarmTime.collectAsState() // 현재 설정된 알림 시각


    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp) // FAB 사이의 간격
            ) {
                // 알림 설정 FAB
                FloatingActionButton(onClick = { showTimePicker.value = true }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "알림 설정")
                        Text(
                            text = if (alarmTime.isNotEmpty()) "$alarmTime" else "알림 없음",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // 알림 취소 FAB
                FloatingActionButton(onClick = { mainViewModel.cancelAlarm() }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = "알림 취소")
                    }
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
                viewModel = viewModel,
                mainViewModel = mainViewModel
            )
        }
    }
    // 시간 선택 다이얼로그 표시
    if (showTimePicker.value) {
        TimePickerExample(showTimePicker = showTimePicker, onTimeSelected = { hour, minute ->
            // 시간 선택 후 처리 로직
            showTimePicker.value = false
            // 예: ViewModel을 통해 알림 시간 설정
            mainViewModel.setAlarmTime(hour, minute)
        })
    }
}
@Composable
fun TimePickerExample(showTimePicker: MutableState<Boolean>, onTimeSelected: (Int, Int) -> Unit) {
    Log.d("TimePickerExample", " launch")
    val context = LocalContext.current

    if (showTimePicker.value) {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = currentTime.hour
        val minute = currentTime.minute

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                onTimeSelected(selectedHour, selectedMinute)
                showTimePicker.value = false // 시간이 선택되면 TimePicker를 숨깁니다.
            },
            hour,
            minute,
            true // 24시간 형식 사용 여부
        )

        timePickerDialog.setOnCancelListener {
            showTimePicker.value = false // 취소되면 TimePicker를 숨깁니다.
        }

        timePickerDialog.show()
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
    userList: List<User>,
    isUserScreen: Boolean = false,
    viewModel: UserEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser = homeViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Person, // 예시 아이콘, 변경 가능
                contentDescription = "사용자 선택",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            LazyRow {
                items(userList.size) { index ->
                    val user = userList[index]
                    Button(
                        modifier = Modifier.padding(end = 4.dp),
                        onClick = {
                            coroutineScope.launch {
                                homeViewModel.updateCurrentUser(user)
                                viewModel.updateUserSelectedTime()
                                Toast.makeText(context, "사용자를 ${user.name}(으)로 변경합니다.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user == currentUser.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            contentColor = if (user == currentUser.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(user.name)
                    }

                }
            }
            if (isUserScreen) {
                Button(
                    onClick = {
                        Toast.makeText(context, "새 사용자를 등록합니다.", Toast.LENGTH_SHORT).show()
                        viewModel.initUser()
                    }) {
                    Text("+")
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
    mainViewModel: MainViewModel,
    viewModel: UserEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
//    val uiState = viewModel.uiState.collectAsState()
    val isAgreedPrivacyPolicy by mainViewModel.isAgreedPrivatyPolicy.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
        UserEntryBody(
            userUiState = viewModel.userUiState,
//            userUiState = uiState.value,
            onUserValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveUser()
                    Toast.makeText(context, "사용자 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    viewModel.initUser()
                    navigateBack()
                }
            },
            onCancleClick = {
                    Log.d("onCancle", "${viewModel.userUiState.toString()}")
                    viewModel.initUser()
                    navigateBack()
            },
//            onDeleteClick = {
//                coroutineScope.launch {
//                    viewModel.deleteUser(viewModel.userUiState.userDetails.toUser())
//                    navigateBack()
//                }
//            },
            onDeleteClick = {
                showDeleteConfirmDialog = true
            },
            isAgreedPrivacyPolicy = isAgreedPrivacyPolicy,
            onAgreePrivacyPolicyChanged = { agreed ->
                mainViewModel.setAgreedPrivacyPolicy(agreed)
            },
            onPrivacyPolicyClick = { showPrivacyPolicyDialog = true },
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
        if (showPrivacyPolicyDialog) {
            PrivacyPolicyDialog(
                onDismissRequest = { showPrivacyPolicyDialog = false },
                onConfirmPolicy = {
                    mainViewModel.setAgreedPrivacyPolicy(true)
                    showPrivacyPolicyDialog = false
                }
            )
        }
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("삭제 확인") },
                text = { Text("정말로 사용자 "+ viewModel.userUiState.userDetails.name +"을 삭제하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteUser(viewModel.userUiState.userDetails.toUser())
                                Toast.makeText(context, "사용자 ${viewModel.userUiState.userDetails.name}을 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                                navigateBack()
                                showDeleteConfirmDialog = false
                            }
                        }
                    ) { Text("예") }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmDialog = false }) { Text("아니오") }
                }
            )
        }
    }
}

@Composable
fun UserEntryBody(
    userUiState: UserUiState,
    onUserValueChange: (UserDetails) -> Unit,
    onSaveClick: () -> Unit,
    onCancleClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isAgreedPrivacyPolicy: Boolean,
    onAgreePrivacyPolicyChanged: (Boolean) -> Unit,
    onPrivacyPolicyClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var agreeToPrivacyPolicy by remember { mutableStateOf(isAgreedPrivacyPolicy) }
    LaunchedEffect(isAgreedPrivacyPolicy) {
        agreeToPrivacyPolicy = isAgreedPrivacyPolicy
    }


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
        if (agreeToPrivacyPolicy) {
            Row(
                verticalAlignment = Alignment.CenterVertically // 수직 방향으로 가운데 정렬
            ) {
                Checkbox(
                    checked = agreeToPrivacyPolicy,
                    onCheckedChange = {
                        agreeToPrivacyPolicy = it
                        onAgreePrivacyPolicyChanged(it)
                    }
                )
                Text("개인정보 방침에 동의함")
            }
        }
        // Show the privacy policy button only when it's not agreed yet
        else {
            Button(onClick = {
                onPrivacyPolicyClick(true)
            }) {
                Text("개인정보 방침 확인")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onSaveClick,
                enabled = userUiState.isEntryValid && isAgreedPrivacyPolicy,
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
                enabled = userUiState.userDetails.name.isNotEmpty(),
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


@Composable
fun PrivacyPolicyDialog(onDismissRequest: () -> Unit, onConfirmPolicy: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Privacy Policy") },
        text = {
            LazyColumn {
                item {
                    Text("1. 개인정보 수집 및 이용\n" +
                            "근시야는 영유아 자녀를 둔 보호자를 대상으로 하며, 사용자에게 최적의 서비스를 제공하기 위하여 다음과 같은 개인정보를 수집하고 있습니다:\n\n" +
                            "수집하는 개인정보 항목: 사용자의 이름, 생년월일, 병원코드, 환자코드; 영유아 자녀에 관한 정보; 점안 데이터 및 활동 데이터\n" +
                            "수집 목적: 의료 서비스 제공, 연구용 데이터 활용, 점안 데이터 및 활동 데이터를 환자정보와 연결하여 연구용 분석 및 의료 서비스 품질 향상\n" +
                            "데이터 수집 방법: 앱을 통한 사용자 직접 입력 및 서버로부터의 데이터 취합\n\n" +
                            "2. 개인정보의 보관 및 이용 기간\n" +
                            "귀하의 개인정보는 법률 또는 사용자의 동의에 의해 보관되는 기간 동안만 저장 및 이용됩니다:\n\n" +
                            "보관 기간: 사용자 요청이 없는 경우 최대 3년간\n\n" +
                            "3. 개인정보의 처리 및 보호\n" +
                            "근시야는 사용자의 개인정보를 보호하고 관련 법률 및 규정을 준수하기 위해 다음과 같은 조치를 취하고 있습니다:\n\n" +
                            "개인정보 처리 및 관리 방법에 대한 내부 지침 수립 및 시행\n" +
                            "개인정보에 대한 접근 제한 및 이용 권한 관리\n" +
                            "개인정보 보호를 위한 기술적, 관리적 보호 조치\n\n" +
                            "4. 개인정보의 제3자 제공 및 공유\n" +
                            "근시야는 법률에 의한 경우를 제외하고, 사용자의 동의 없이 개인정보를 제3자에게 제공하거나 공유하지 않습니다.\n\n" +
                            "5. 개인정보처리방침의 변경\n" +
                            "본 개인정보처리방침은 법률, 정책 또는 보안 기술의 변경에 따라 업데이트될 수 있으며, 변경 사항은 근시야 웹사이트 또는 앱 공지사항을 통해 공지됩니다.")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirmPolicy() // This will be called when the confirm button is clicked
            }) {
                Text("동의함") // Or "Agree" in English
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("취소")
            }
        }
    )
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
    val context = LocalContext.current

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
            keyboardActions = KeyboardActions(onNext = {
                val isValidDate = when (userDetails.birthDate.length) {
                    6 -> {
                        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val currentYearLastTwoDigits = currentDate.year % 100
                        val yearPrefix = if (userDetails.birthDate.substring(0, 2).toInt() < currentYearLastTwoDigits) "20" else "19"
                        val fullYearBirthDate = yearPrefix + userDetails.birthDate

                        validateAndApplyBirthDate(context, fullYearBirthDate, onValueChange, userDetails)
                    }
                    8 -> {
                        validateAndApplyBirthDate(context, userDetails.birthDate, onValueChange, userDetails)
                    }
                    else -> {
                        Toast.makeText(context, "생년월일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                        false
                    }
                }

                if (isValidDate) {
                    hospitalCodeFocusRequester.requestFocus()
                }
            }),
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

fun validateAndApplyBirthDate(
    context: Context,
    birthDate: String,
    onValueChange: (UserDetails) -> Unit,
    userDetails: UserDetails
): Boolean {
    val month = birthDate.substring(4, 6).toInt()
    val day = birthDate.substring(6, 8).toInt()

    return if (month > 12 || day > 31) {
        Toast.makeText(context, "생년월일을 재확인해주세요.", Toast.LENGTH_SHORT).show()
        false
    } else {
        onValueChange(userDetails.copy(birthDate = birthDate))
        true
    }
}

