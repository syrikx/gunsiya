package com.hyunakim.gunsiya

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 알림 표시
        val notificationHelper = NotificationHelper(context)
        val notification = notificationHelper.getChannelNotification()
        notificationHelper.getManager().notify(1, notification.build())

        // 다음 날 같은 시간에 알림 설정
//        setNextDayAlarm(context)
    }

//    @SuppressLint("ScheduleExactAlarm")
//    private fun setNextDayAlarm(context: Context) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        val calendar = Calendar.getInstance().apply {
//            add(Calendar.DATE, 1) // 현재 날짜에서 하루를 더함
//        }
//
//        val intent = Intent(context, AlertReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//    }
}

