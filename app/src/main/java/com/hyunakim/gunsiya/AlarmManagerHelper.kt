package com.hyunakim.gunsiya

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmManagerHelper(private val context: Context) {

    fun startAlarm(calendar: Calendar) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }
}
