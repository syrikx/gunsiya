package com.hyunakim.gunsiya

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        val notification = notificationHelper.getChannelNotification()
        notificationHelper.getManager().notify(1, notification.build())
    }
}

