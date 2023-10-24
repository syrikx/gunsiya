package com.hyunakim.gunsiya.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.ui.component.day.KalendarDayKonfig
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.theme.KalendarTheme
import com.hyunakim.gunsiya.ui.user.UserSelect
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
            .fillMaxSize()
    )


}

fun logDay(day : LocalDate){
    Log.d("syrikx", "selected date : $day")
}

@Composable
fun HomeBody(userList: List<User>,
//             onItemClick: (Int) -> Unit,
             modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (userList.isEmpty()) {
            Text(
                text = "등록된 유저가 없습니다.",
                textAlign = TextAlign.Center
            )
        } else {
            UserSelect(userList)
        }
        KalendarTheme {
            Column {
//                    val events = (0..5).map {
//                        KalendarEvent(
//                            date = Clock.System.todayIn(
//                                TimeZone.currentSystemDefault()
//                            ).plus(it, DateTimeUnit.DAY),
//                            eventName = it.toString(),
//                        )
//                    }
//                    val events1 = (0..5).map {
//                        com.himanshoe.kalendar.KalendarEvent(
//                            date = Clock.System.todayIn(
//                                TimeZone.currentSystemDefault()
//                            ).plus(it, DateTimeUnit.DAY),
//                            eventName = it.toString(),
//                        )
//                    }
//                    com.himanshoe.kalendar.Kalendar(
//                        currentDay = Clock.System.todayIn(
//                            TimeZone.currentSystemDefault()
//                        ),
//                        kalendarType = KalendarType.Oceanic,
//                        events = com.himanshoe.kalendar.KalendarEvents(events1 + events1 + events1)
//
//                    )
//                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
//                    com.himanshoe.kalendar.Kalendar(
//                        currentDay = Clock.System.todayIn(
//                            TimeZone.currentSystemDefault()
//                        ),
//                        kalendarType = KalendarType.Firey,
//                        daySelectionMode = DaySelectionMode.Range,
//                        events = com.himanshoe.kalendar.KalendarEvents(events1 + events1 + events1),
//                        onRangeSelected = { range, rangeEvents ->
//                            Log.d(":SDfsdfsdfdsfsdfsdf",range.toString())
//                            Log.d(":SDfsdfsdfdsfsdfsdf",rangeEvents.toString())
//                        }
//                    )
//                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                val today = LocalDate
                Kalendar(
//                    currentDay = null,
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
                        // Handle day click event
                        logDay(selectedDay)
                    },
                    onRangeSelected = { selectedRange, events ->
                        // Handle range selection event
                    },
                    onErrorRangeSelected = { error ->
                        // Handle error
                    })

            }

        }
    }
}