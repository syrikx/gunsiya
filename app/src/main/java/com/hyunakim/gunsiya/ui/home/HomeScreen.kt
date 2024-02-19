package com.hyunakim.gunsiya.ui.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himanshoe.kalendar.KalendarWithEvent
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.ui.component.day.KalendarDayKonfig
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import com.hyunakim.gunsiya.data.Record
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.MainViewModel
import com.hyunakim.gunsiya.ui.result.saveLastUploadTime
import com.hyunakim.gunsiya.ui.theme.KalendarTheme
import com.hyunakim.gunsiya.ui.user.UserSelect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.todayIn

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
//    navigateToItemUpdate :(Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    mainViewModel: MainViewModel
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
//    val currentUser by GunsiyaApplication.UserManager.currentUser.collectAsState()
//    Text("${viewModel.currentUser.collectAsState().value.name}님 환영합니다.")
    HomeBody(
        mainViewModel = mainViewModel,
        userList = homeUiState.userList,
//        onItemClick = navigateToItemUpdate,
        modifier = Modifier
            .fillMaxSize(),
        viewModel = viewModel
    )
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

@Composable
fun HomeBody(
    userList: List<User>,
//             onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier, viewModel: HomeViewModel, mainViewModel: MainViewModel
){
//    val selectedDate = remember { mutableStateOf(viewModel.recordsState) }
    var selectedMonthLastDay by remember { mutableStateOf(Clock.System.todayIn(
        TimeZone.currentSystemDefault()
    ))}
    val currentDay = Clock.System.todayIn(
        TimeZone.currentSystemDefault()
    )
    var selectedMonthDays by remember { mutableStateOf(selectedMonthLastDay.dayOfMonth) }
    val recordsState = viewModel.recordsState.collectAsState()
    var coloredDates = getAtropineDropDates(recordsState.value)
    var coloredDatesCount by remember { mutableStateOf(0) }
    coloredDatesCount = coloredDates.count {
        it.year == selectedMonthLastDay.year && it.monthNumber == selectedMonthLastDay.monthNumber
    }
    var totalOutdoorTime = getMonthlyOutdoorActivityTime(recordsState.value.recordList, selectedMonthLastDay.year, selectedMonthLastDay.monthNumber)
    var totalCloseWorkTime = getMonthlyCloseWorkTime(recordsState.value.recordList, selectedMonthLastDay.year, selectedMonthLastDay.monthNumber)
//    LaunchedEffect(recordsState.value) {
//        // recordsState 값이 변경될 때마다 실행되는 코드
//        coloredDates = getAtropineDropDates(recordsState.value)
//        coloredDatesCount = coloredDates.count {
//            it.year == currentDay.year && it.monthNumber == currentDay.monthNumber
//        }
//        totalOutdoorTime = getMonthlyOutdoorActivityTime(recordsState.value.recordList, currentDay.year, currentDay.monthNumber)
//        totalCloseWorkTime = getMonthlyCloseWorkTime(recordsState.value.recordList, currentDay.year, currentDay.monthNumber)
//    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val coroutineScope = rememberCoroutineScope()
        var showDialog by remember { mutableStateOf(false) }
        val isAgreedPrivacyPolicy by mainViewModel.isAgreedPrivatyPolicy.collectAsState()
        if (userList.isEmpty()) {
            Text(
                text = "먼저 USER 탭에서 사용자 등록 하세요",
                textAlign = TextAlign.Center
            )
        } else {
            UserSelect(userList)
        }
        var recordSelected : Record?
        KalendarTheme {
            Column {

                KalendarWithEvent(
                    currentDay = currentDay,
                    kalendarType = KalendarType.Firey,
                    modifier = Modifier.padding(0.dp),
                    showLabel = true,
                    events = KalendarEvents(),
                    kalendarHeaderTextKonfig = null,
                    kalendarColors = KalendarColors.default(),
                    kalendarDayKonfig = KalendarDayKonfig.default(),
                    daySelectionMode = DaySelectionMode.Single,
                    dayContent = null,
                    headerContent = null,
                    onDayClick = { selectedDay, events ->
                        viewModel.updateSelectedDay(selectedDay)
                        viewModel.getSelectedRecord(recordsState.value,selectedDay)
//                        recordSelected = viewModel.getSelectedRecord(recordsState, selectedDay)
//                        Log.d("date", recordsState.value.toString())
                        Log.d("date", viewModel.selectedRecord.value.toString())
                    },
                    onRangeSelected = { selectedRange, events ->
                        // Handle range selection event
                    },
                    onErrorRangeSelected = { error ->
                        // Handle error
                    },
                    coloredDates = coloredDates,
                    onMonthChange = { year, month ->
                        val firstDayOfNextMonth = if (month == Month(12)) {
                            LocalDate(year + 1, Month(1), 1)
                        } else {
                            LocalDate(year, month.number + 1, 1)
                        }
                        selectedMonthLastDay = firstDayOfNextMonth.minus(1, DateTimeUnit.DAY)
                        selectedMonthDays = selectedMonthLastDay.dayOfMonth
                        if (year == currentDay.year && month.number == currentDay.monthNumber) {
                            selectedMonthDays = currentDay.dayOfMonth
                        }
//                        coloredDatesCount = coloredDates.count {
//                            it.year == year && it.monthNumber == month.number
//                        }
//                        totalOutdoorTime = getMonthlyOutdoorActivityTime(recordsState.value.recordList, selectedMonthLastDay.year, selectedMonthLastDay.monthNumber)
//                        totalCloseWorkTime = getMonthlyCloseWorkTime(recordsState.value.recordList, selectedMonthLastDay.year, selectedMonthLastDay.monthNumber)
//                        Log.d("HomeScreen", "Last day of month changed to: $selectedMonthLastDay")
                    }
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(3f) // 텍스트가 차지할 공간
            ) {
                Text(
                    text = "${selectedMonthLastDay.monthNumber}월 점안율 : ${(coloredDatesCount * 100 / selectedMonthDays)}% (${selectedMonthDays}일 중 ${coloredDatesCount}일)",
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "선택 : ${viewModel.selectedDay.collectAsState().value}일",
                    textAlign = TextAlign.Start
                )
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f) // 돋보기 아이콘이 차지할 공간
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Search, contentDescription = "더 자세히 알아보기")
                }
            }

        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))




//        var isAtropineDrop by remember { mutableStateOf(false) }
        var isAtropineDrop = viewModel.selectedRecord.collectAsState().value.isAtropineDrop
        var timeOutdoorActivity = viewModel.selectedRecord.collectAsState().value.timeOutdoorActivity
        var timeCloseWork = viewModel.selectedRecord.collectAsState().value.timeCloseWork

        Row (
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "아트로핀 점안  ",
                textAlign = TextAlign.Center
            )
            Switch(
                checked = isAtropineDrop,
                onCheckedChange = { isChecked ->
                    isAtropineDrop = isChecked
                    updateAndSaveRecord(isAtropineDrop, timeOutdoorActivity, timeCloseWork, viewModel, coroutineScope)
                }
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "야외 활동 : ${timeOutdoorActivity}시간  ")
            Slider(
                value = timeOutdoorActivity.toFloat(),
                onValueChange = { newValue ->
                    timeOutdoorActivity = newValue.toDouble()
                    updateAndSaveRecord(isAtropineDrop, timeOutdoorActivity, timeCloseWork, viewModel, coroutineScope)
                },
                valueRange = 0.0f..8.0f,
                steps = 15
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "근접 작업 : ${timeCloseWork}시간  ")
            Slider(
                value = timeCloseWork.toFloat(),
                onValueChange = { newValue ->
                    timeCloseWork = newValue.toDouble()
                    updateAndSaveRecord(isAtropineDrop, timeOutdoorActivity, timeCloseWork, viewModel, coroutineScope)
                },
                valueRange = 0.0f..8.0f,
                steps = 15
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val hasChanges by viewModel.hasChanges.collectAsState()
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.uploadUsers()
                        saveLastUploadTime(System.currentTimeMillis())
                    }
                },
                enabled = hasChanges && isAgreedPrivacyPolicy && userList.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "업로드")
            }
        }
        // 상세 정보 다이얼로그
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("${selectedMonthLastDay.year.toString().substring(2,4)}년 ${selectedMonthLastDay.monthNumber}월 월간 정보") },
                text = {
                    // 여기에 월간 점안일 수, 월간 야외활동 시간, 월간 근접 작업 시간 등의 정보를 포함시킵니다.
                    Text("월간 점안일 수 : ${coloredDatesCount}일\n월간 야외활동 시간 : ${totalOutdoorTime}시간 \n월간 근접 작업 시간 : ${totalCloseWorkTime}시간")
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }
    }
}


private fun getAtropineDropDates(recordsState: HomeViewModel.RecordsState): List<LocalDate> {
    return recordsState.recordList.filter { it.isAtropineDrop }.map { LocalDate.parse(it.date) }
}

private fun getRecordSelected(recordsState: HomeViewModel.RecordsState, selectedDate : LocalDate): Record? {
    return recordsState.recordList.firstOrNull { LocalDate.parse(it.date) == selectedDate }
}

private fun getOutdoorActivityTimes(recordsState: HomeViewModel.RecordsState): List<Double> {
    return recordsState.recordList.map { it.timeOutdoorActivity }
}

private fun getCloseWorkTimes(recordsState: HomeViewModel.RecordsState): List<Double> {
    return recordsState.recordList.map { it.timeCloseWork }
}

fun getMonthlyOutdoorActivityTime(records: List<Record>, year: Int, month: Int): Double {
    val outdoorActivityTimes = records.filter {
        val recordDate = LocalDate.parse(it.date)
        recordDate.year == year && recordDate.monthNumber == month
    }.sumOf { it.timeOutdoorActivity }
    Log.d("getMonthlyOutdoorActivityTime", "${year} ${month} ${records} ${outdoorActivityTimes}")
    return outdoorActivityTimes
}

fun getMonthlyCloseWorkTime(records: List<Record>, year: Int, month: Int): Double {
    return records.filter {
        val recordDate = LocalDate.parse(it.date)
        recordDate.year == year && recordDate.monthNumber == month
    }.sumOf { it.timeCloseWork }
}



//private fun updateRecord(viewModel : HomeViewModel, isAtropineDrop : Boolean, timeOutdoorActivity: Int, timeCloseWork: Int) {
//    val record = com.hyunakim.gunsiya.data.Record(
//        userId = viewModel.currentUser.value.id,
//        date = viewModel.selectedDay.value.toString(),
//        isAtropineDrop = isAtropineDrop,
//        timeOutdoorActivity = timeOutdoorActivity,
//        timeCloseWork = timeCloseWork
//    )
//    viewModel.updateSelectedRecord(record)
//
//}
//val viewModel: YourViewModel = // viewModel의 정의
//val coroutineScope = rememberCoroutineScope()

//private fun updateRecord(isChecked: Boolean) {
//    isAtropineDrop = isChecked
//    val record = com.hyunakim.gunsiya.data.Record(
//        userId = viewModel.currentUser.value.id,
//        date = viewModel.selectedDay.value.toString(),
//        isAtropineDrop = isAtropineDrop,
//        timeOutdoorActivity = timeOutdoorActivity,
//        timeCloseWork = timeCloseWork
//    )
//    viewModel.updateSelectedRecord(record)
//    coroutineScope.launch {
//        viewModel.saveRecord()
//    }
//}
private fun updateAndSaveRecord(
    isAtropineDrop: Boolean,
    timeOutdoorActivity: Double,
    timeCloseWork: Double,
    viewModel: HomeViewModel,
    coroutineScope: CoroutineScope
) {
//    coroutineScope.launch {
//        val currentUser = GunsiyaApplication.UserManager.currentUser.firstOrNull()
//        val userId = currentUser?.id ?: 0
//
//        val record = com.hyunakim.gunsiya.data.Record(
//            userId = userId,
//            date = viewModel.selectedDay.value.toString(),
//            isAtropineDrop = isAtropineDrop,
//            timeOutdoorActivity = timeOutdoorActivity,
//            timeCloseWork = timeCloseWork
//        )
//
//        // 'record' 객체를 사용하는 로직
//        // 예: 데이터베이스 업데이트, API 호출 등
//    }

    val record = com.hyunakim.gunsiya.data.Record(
        userId = viewModel.currentUser.value.id,
        date = viewModel.selectedDay.value.toString(),
        isAtropineDrop = isAtropineDrop,
        timeOutdoorActivity = timeOutdoorActivity,
        timeCloseWork = timeCloseWork
    )
    viewModel.updateSelectedRecord(record)
    coroutineScope.launch {
        viewModel.saveRecord()
    }
}