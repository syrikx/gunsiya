/*
 * Copyright 2023 Kalendar Contributors (https://www.himanshoe.com). All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.himanshoe.kalendar.ui.firey

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.himanshoe.kalendar.KalendarEvent
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.ui.component.day.KalendarDay
import com.himanshoe.kalendar.ui.component.day.KalendarDayKonfig
import com.himanshoe.kalendar.ui.component.header.KalendarHeader
import com.himanshoe.kalendar.ui.component.header.KalendarTextKonfig
import com.himanshoe.kalendar.ui.oceanic.util.isLeapYear
import com.himanshoe.kalendar.util.MultiplePreviews
import com.himanshoe.kalendar.util.onDayClicked
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.todayIn
import java.lang.Math.abs

private val WeekDays = listOf("월", "화", "수", "목", "금", "토", "일")

/**
 * Internal composable function representing the Kalendar component.
 *
 * @param currentDay The current selected day in the Kalendar.
 * @param daySelectionMode The day selection mode in the Kalendar.
 * @param modifier The modifier for styling or positioning the Kalendar.
 * @param showLabel Determines whether to show labels in the Kalendar.
 * @param kalendarHeaderTextKonfig The configuration for the Kalendar header text.
 * @param kalendarColors The colors configuration for the Kalendar.
 * @param events The events associated with the Kalendar.
 * @param kalendarDayKonfig The configuration for each day in the Kalendar.
 * @param dayContent Custom content for rendering each day in the Kalendar.
 * @param headerContent Custom content for rendering the header of the Kalendar.
 * @param onDayClick Callback invoked when a day is clicked.
 * @param onRangeSelected Callback invoked when a range of days is selected.
 * @param onErrorRangeSelected Callback invoked when an error occurs during range selection.
 */
@Composable
internal fun KalendarFirey(
    currentDay: LocalDate?,
    daySelectionMode: DaySelectionMode,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    kalendarHeaderTextKonfig: KalendarTextKonfig? = null,
    kalendarColors: KalendarColors = KalendarColors.default(),
    events: KalendarEvents = KalendarEvents(),
    kalendarDayKonfig: KalendarDayKonfig = KalendarDayKonfig.default(),
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
    headerContent: (@Composable (Month, Int) -> Unit)? = null,
    onDayClick: (LocalDate, List<KalendarEvent>) -> Unit = { _, _ -> },
    onRangeSelected: (KalendarSelectedDayRange, List<KalendarEvent>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {},
    coloredDates : List<LocalDate> = listOf(Clock.System.todayIn(TimeZone.currentSystemDefault()).plus(1,DateTimeUnit.DAY), Clock.System.todayIn(TimeZone.currentSystemDefault()).plus(2,DateTimeUnit.DAY)),
    onMonthChange: (year: Int, month: Month) -> Unit
) {
    val today = currentDay ?: Clock.System.todayIn(TimeZone.currentSystemDefault())
    val selectedRange = remember { mutableStateOf<KalendarSelectedDayRange?>(null) }
    val selectedDate = remember { mutableStateOf(today) }
    val displayedMonth = remember { mutableStateOf(today.month) }
    val displayedYear = remember { mutableStateOf(today.year) }
    val currentMonth = displayedMonth.value
    val currentYear = displayedYear.value
    val currentMonthIndex = currentMonth.value.minus(1)

    val defaultHeaderColor = KalendarTextKonfig.default(
        color = kalendarColors.color[currentMonthIndex].headerTextColor,
    )
    val newHeaderTextKonfig = kalendarHeaderTextKonfig ?: defaultHeaderColor

    val daysInMonth = currentMonth.length(currentYear.isLeapYear())
    val monthValue = currentMonth.value.toString().padStart(2, '0')
    val startDayOfMonth = "$currentYear-$monthValue-01".toLocalDate()
    val firstDayOfMonth = startDayOfMonth.dayOfWeek
    val dragAmountState = remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .background(
                color = kalendarColors.color[currentMonthIndex].backgroundColor
            )
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .pointerInput(Unit) {

                detectHorizontalDragGestures(onDragStart = { offset ->
                    dragAmountState.value = 0f  // 드래그 시작 시 드래그 양 초기화
                }, onDragEnd = {
                    // 드래그 종료 시 월 변경 처리
                    val dragThreshold = 200f // 드래그 임계값 설정
                    // 오른쪽으로 드래그: 이전 달로 전환
                    if (dragAmountState.value > dragThreshold) {
                        if (displayedMonth.value == Month.JANUARY) {
                            displayedYear.value -= 1
                            displayedMonth.value = Month.DECEMBER
                        } else {
                            displayedMonth.value = displayedMonth.value.minus(1)
                        }
                        onMonthChange(displayedYear.value, displayedMonth.value)
                    }
// 왼쪽으로 드래그: 다음 달로 전환
                    else if (dragAmountState.value < -dragThreshold) {
                        if (displayedMonth.value == Month.DECEMBER) {
                            displayedYear.value += 1
                            displayedMonth.value = Month.JANUARY
                        } else {
                            displayedMonth.value = displayedMonth.value.plus(1)
                        }
                        onMonthChange(displayedYear.value, displayedMonth.value)
                    }
                }) { change, dragAmount ->
                    dragAmountState.value += dragAmount  // 드래그 양 누적
                    change.consume()
                }
            }


    ) {
        if (headerContent != null) {
            headerContent(currentMonth, currentYear)
        } else {
            KalendarHeader(
                month = currentMonth,
                year = currentYear,
                kalendarTextKonfig = newHeaderTextKonfig,
                onPreviousClick = {
                    displayedYear.value -= if (currentMonth == Month.JANUARY) 1 else 0
                    displayedMonth.value -= 1
                    onMonthChange(displayedYear.value, displayedMonth.value)
                },
                onNextClick = {
                    displayedYear.value += if (currentMonth == Month.DECEMBER) 1 else 0
                    displayedMonth.value += 1
                    onMonthChange(displayedYear.value, displayedMonth.value)
                },
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 0.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(7),
            content = {
                if (showLabel) {
                    items(WeekDays) { item ->
                        Text(
                            modifier = Modifier,
                            color = kalendarDayKonfig.textColor,
                            fontSize = kalendarDayKonfig.textSize,
                            text = item,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                items((getFirstDayOfMonth(firstDayOfMonth)..daysInMonth).toList()) {
                    if (it > 0) {
                        val day = calculateDay(it, currentMonth, currentYear)
                        if (dayContent != null) {
                            dayContent(day)
                        } else {
                            KalendarDay(
                                date = day,
                                colored = checkDateColored(day, coloredDates),
                                selectedDate = selectedDate.value,
                                kalendarColors = kalendarColors.color[currentMonthIndex],
                                kalendarEvents = events,
                                kalendarDayKonfig = kalendarDayKonfig,
                                selectedRange = selectedRange.value,
                                onDayClick = { clickedDate, event ->
                                    onDayClicked(
                                        clickedDate,
                                        event,
                                        daySelectionMode,
                                        selectedRange,
                                        onRangeSelected = { range, events ->
                                            if (range.end < range.start) {
                                                onErrorRangeSelected(RangeSelectionError.EndIsBeforeStart)
                                            } else {
                                                onRangeSelected(range, events)
                                            }
                                        },
                                        onDayClick = { newDate, clickedDateEvent ->
                                            selectedDate.value = newDate
                                            onDayClick(newDate, clickedDateEvent)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
        Spacer(modifier = Modifier.padding(vertical = 0.dp))
    }
}

/**
 * Calculates the offset to determine the first day of the month based on the provided first day of the month.
 *
 * @param firstDayOfMonth The first day of the month.
 * @return The offset value representing the first day of the month.
 */
private fun getFirstDayOfMonth(firstDayOfMonth: DayOfWeek) = -(firstDayOfMonth.value).minus(2)

/**
 * Calculates a LocalDate object based on the provided day, current month, and current year.
 *
 * @param day The day of the month.
 * @param currentMonth The current month.
 * @param currentYear The current year.
 * @return The LocalDate object representing the specified day, month, and year.
 */
private fun calculateDay(day: Int, currentMonth: Month, currentYear: Int): LocalDate {
    val monthValue = currentMonth.value.toString().padStart(2, '0')
    val dayValue = day.toString().padStart(2, '0')
    return "$currentYear-$monthValue-$dayValue".toLocalDate()
}

private fun checkDateColored(date: LocalDate, coloredDates: List<LocalDate>): Boolean {
    return date in coloredDates
}

//@Composable
//@MultiplePreviews
//private fun KalendarFireyPreview() {
//    KalendarFirey(
//        currentDay = Clock.System.todayIn(
//            TimeZone.currentSystemDefault()
//        ),
//        kalendarHeaderTextKonfig = KalendarTextKonfig.previewDefault(),
//        daySelectionMode = DaySelectionMode.Range
//    )
//}
