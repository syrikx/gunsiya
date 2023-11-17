package com.hyunakim.gunsiya.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
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
import com.hyunakim.gunsiya.ui.theme.KalendarTheme
import com.hyunakim.gunsiya.ui.user.UserSelect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
//    navigateToItemUpdate :(Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    Text("${viewModel.currentUser.collectAsState().value.name}님 환영합니다.")
    HomeBody(
        userList = homeUiState.userList,
//        onItemClick = navigateToItemUpdate,
        modifier = Modifier
            .fillMaxSize(),
        viewModel
    )


}

@Composable
fun HomeBody(userList: List<User>,
//             onItemClick: (Int) -> Unit,
             modifier: Modifier = Modifier, viewModel: HomeViewModel){
//    val selectedDate = remember { mutableStateOf(viewModel.recordsState) }
    val recordsState = viewModel.recordsState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val coroutineScope = rememberCoroutineScope()
        if (userList.isEmpty()) {
            Text(
                text = "등록된 유저가 없습니다.",
                textAlign = TextAlign.Center
            )
        } else {
            UserSelect(userList)
        }
        var recordSelected : Record?
        KalendarTheme {
            Column {
                val today = LocalDate
                KalendarWithEvent(
                    currentDay = Clock.System.todayIn(
                        TimeZone.currentSystemDefault()
                    ),
                    kalendarType = KalendarType.Firey,
                    modifier = Modifier,
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
                    coloredDates = getAtropineDropDates(recordsState.value))
            }

        }
        Text(
            text = "선택 : ${viewModel.selectedDay.collectAsState().value}일",
            textAlign = TextAlign.Center
        )

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
            Text(text = "야외 활동 : ${timeOutdoorActivity.toInt()}시간  ")
            Slider(
                value = timeOutdoorActivity.toFloat(),
                onValueChange = { newValue ->
                    timeOutdoorActivity = newValue.toInt()
                    updateAndSaveRecord(isAtropineDrop, timeOutdoorActivity, timeCloseWork, viewModel, coroutineScope)
                },
                valueRange = 0f..20f,
                steps = 20
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
                    timeCloseWork = newValue.toInt()
                    updateAndSaveRecord(isAtropineDrop, timeOutdoorActivity, timeCloseWork, viewModel, coroutineScope)
                },
                valueRange = 0f..20f,
                steps = 20
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
    timeOutdoorActivity: Int,
    timeCloseWork: Int,
    viewModel: HomeViewModel,
    coroutineScope: CoroutineScope
) {
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