package com.hyunakim.gunsiya.ui

import androidx.lifecycle.ViewModel
import com.hyunakim.gunsiya.AlarmManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel(alarmManagerHelper: AlarmManagerHelper) : ViewModel() {
    // 현재 설정된 알림 시각을 관리하는 상태
    private val _alarmTime = MutableStateFlow("")
    val alarmTime = _alarmTime.asStateFlow()

    // 알림 시각을 설정하는 함수
    fun setAlarmTime(hour: Int, minute: Int) {
        // 선택된 시간을 "HH:mm" 형식의 문자열로 변환
        val formattedTime = String.format("%02d:%02d", hour, minute)
        _alarmTime.value = formattedTime
    }

    // 알림을 취소하는 함수
    fun cancelAlarm() {
        _alarmTime.value = ""
        // 여기에 알림 취소 로직을 추가
    }
}
