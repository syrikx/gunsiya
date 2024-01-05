package com.hyunakim.gunsiya

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {
    private val channelId = "alarm_channel"
    private val channelName = "Alarm Channel"

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.RED
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun getChannelNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Alarm!")
            .setContentText("Your alarm is ringing!")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    fun getManager(): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}

