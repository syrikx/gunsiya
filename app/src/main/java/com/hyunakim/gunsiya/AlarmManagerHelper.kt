package com.hyunakim.gunsiya

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import java.util.Calendar

class AlarmManagerHelper(private val context: Context) {
    init {
        Log.d("Instance created", "AlarmManagerHelper Instance created: $this")
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlertReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    val areNotificationsEnabled = notificationManager.areNotificationsEnabled()

//    val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    @SuppressLint("ScheduleExactAlarm")
//    fun setAlarm(calendar: Calendar) {
//        if (!areNotificationsEnabled) {
//            // 알림이 비활성화된 경우
//            Log.d("setAlarm : ", context.packageName)
//            val intent = Intent().apply {
//                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
//                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//            }
//            context.startActivity(intent)
//        }
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//    }

    fun setInexactRepeatingAlarm(calendar: Calendar, intervalMillis: Long) {

        if (!notificationManager.areNotificationsEnabled()) {
            // 알림이 비활성화된 경우
            Log.d("setInexactRepeatingAlarm : ", context.packageName)
            val intent = Intent().apply {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        }
        // RTC_WAKEUP을 사용하여 기기가 깨어나도록 설정하고, 부정확한 주기적 알람 설정
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intervalMillis,
            pendingIntent
        )
    }

    fun cancelAlarm() {
        alarmManager.cancel(pendingIntent)
    }



//    @SuppressLint("ScheduleExactAlarm")
//    fun setExactAlarm(timeInMillis: Long) {
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
//    }
}
