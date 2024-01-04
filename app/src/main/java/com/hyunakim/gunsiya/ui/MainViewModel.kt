package com.hyunakim.gunsiya.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel : ViewModel() {
    // 현재 설정된 알림 시각을 관리하는 상태
    private val _alarmTime = MutableStateFlow("")
    val alarmTime = _alarmTime.asStateFlow()

    // 알림 시각을 설정하는 함수
    fun setAlarmTime(calendar: Calendar) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        _alarmTime.value = timeFormat.format(calendar.time)
        // 여기에 알림 설정 로직을 추가
    }

    // 알림을 취소하는 함수
    fun cancelAlarm() {
        _alarmTime.value = ""
        // 여기에 알림 취소 로직을 추가
    }
}
