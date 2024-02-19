package com.hyunakim.gunsiya.ui

import android.app.AlarmManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunakim.gunsiya.AlarmManagerHelper
import com.hyunakim.gunsiya.GunsiyaApplication
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.data.UsersRepository
import com.hyunakim.gunsiya.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainViewModel(
    private val alarmManagerHelper: AlarmManagerHelper,
    private val usersRepository: UsersRepository
) : ViewModel() {

    // 현재 설정된 알림 시각을 관리하는 상태
    private val _alarmTime = MutableStateFlow("")
    private val _isAgreedPrivatyPolicy = MutableStateFlow(false)
    val alarmTime = _alarmTime.asStateFlow()
    val isAgreedPrivatyPolicy = _isAgreedPrivatyPolicy.asStateFlow()
    val TIMEOUT_MILLIS = 5_000L
    data class HomeUiState(val userList: List<User> = listOf())
    val homeUiState :StateFlow<HomeUiState> = usersRepository.getAllUsersStream().map {HomeUiState(it)}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    init {
        Log.d("mainViewModel init :", "mainViewModel을 초기화 합니다.")
        loadAlarmTime()
        loadIsAgreedPrivatyPolicy() // 앱 시작 시 개인정보 동의 상태 로드
    }

    // 알림 시각을 설정하는 함수
    fun setAlarmTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
//        alarmManagerHelper.setAlarm(calendar)
        // 부정확한 반복 알람 설정
        cancelAlarm()
        alarmManagerHelper.setInexactRepeatingAlarm(calendar, AlarmManager.INTERVAL_DAY)
        // 선택된 시간을 "HH:mm" 형식의 문자열로 변환
        val formattedTime = String.format("%02d:%02d", hour, minute)
        _alarmTime.value = formattedTime
        // SharedPreferences에 알림 시간 저장
        saveAlarmTime(calendar.timeInMillis)
    }

    // 알림을 취소하는 함수
    fun cancelAlarm() {
        alarmManagerHelper.cancelAlarm()
        _alarmTime.value = ""
        saveAlarmTime(0)
        // 여기에 알림 취소 로직을 추가
    }

    private fun saveAlarmTime(timeInMillis: Long) {
        GunsiyaApplication.sharedPreferences.edit()
            .putLong("alarmTime", timeInMillis)
            .apply()
    }

    fun setAgreedPrivacyPolicy(newValue : Boolean){
        Log.d("setAgreedPrivacyPolicy :", "${newValue}로 변경합니다.")
        _isAgreedPrivatyPolicy.value = newValue
        GunsiyaApplication.sharedPreferences.edit()
            .putBoolean("isAgreedPrivacyPolicy", newValue)
            .apply()
    }

    fun loadAlarmTime() {
        val savedTime = GunsiyaApplication.sharedPreferences.getLong("alarmTime", 0)
        if (savedTime > 0) {
            // 저장된 시간으로 알림 재설정
//            alarmManagerHelper.setExactAlarm(savedTime)
            // 저장된 시간을 Calendar 객체로 변환
            val calendar = Calendar.getInstance().apply {
                timeInMillis = savedTime
            }

            // "HH:mm" 형식으로 시간 형식화
            val formattedTime = String.format("%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )

            // 형식화된 시간을 상태로 설정
            _alarmTime.value = formattedTime
        }
    }

    private fun loadIsAgreedPrivatyPolicy() {
        val isAgreed = GunsiyaApplication.sharedPreferences.getBoolean("isAgreedPrivacyPolicy", false)
        _isAgreedPrivatyPolicy.value = isAgreed
        Log.d("loadIsAgreedPrivacyPolicy :", "현재 동의 상태: $isAgreed")
    }
}
