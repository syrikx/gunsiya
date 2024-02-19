package com.hyunakim.gunsiya

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

//class NotificationHelper(private val context: Context) {
//    private val channelId = "gunsiya_channel"
//    private val channelName = "Gunsiya Channel"
//    private val importance = NotificationManager.IMPORTANCE_DEFAULT
//
//    init {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createChannel()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createChannel() {
//        val channel = NotificationChannel(channelId, channelName, importance).apply {
//            enableLights(true)
//            enableVibration(true)
//            lightColor = Color.RED
//            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//        }
//
//        val manager = context.getSystemService(NotificationManager::class.java)
//        manager.createNotificationChannel(channel)
//    }
//
//    fun getChannelNotification(): NotificationCompat.Builder {
//        return NotificationCompat.Builder(context, channelId)
//            .setContentTitle("Alarm!")
//            .setContentText("Your alarm is ringing!")
//            .setSmallIcon(R.drawable.baseline_notifications_24)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//    }
//
//    fun getManager(): NotificationManager {
//        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//}
class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private val channelId = "91"
    private val channelNm = "92"

    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
        Log.d("Instance created", "NotificationHelper Instance created: $this")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        Log.d("syrikx", "NotificationHelper createChannel run")
        var channel = NotificationChannel(channelId,channelNm, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = Color.GREEN
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager().createNotificationChannel(channel)

    }
    fun getManager() : NotificationManager {
        Log.d("syrikx", "NotificationHelper getManager run")
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    fun getChannelNotification() : NotificationCompat.Builder {
        Log.d("syrikx", "NotificationHelper getChannelNotification run")
        var mainIntent = Intent(this, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        var yesIntent = Intent(this, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra("didDropAtropine", true)
        var noIntent = Intent(this, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra("didDropAtropine", false)
//        Log.d("syrikx8", yesIntent.hasExtra("didDropAtropine").toString())
        var mainPedingIntent = PendingIntent.getActivity(this, 41, mainIntent, PendingIntent.FLAG_IMMUTABLE)
        var yesPedingIntent = PendingIntent.getActivity(this, 42, yesIntent, PendingIntent.FLAG_IMMUTABLE)
        var noPedingIntent = PendingIntent.getActivity(this, 43, noIntent, PendingIntent.FLAG_IMMUTABLE)
        var nb = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("근시야")
            .setContentText("오늘 아트로핀 접안 하셨나요?")
            .setSmallIcon(R.mipmap.gunsiya_round)
            .setContentIntent(mainPedingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText("오늘 아트로핀 접안 하셨나요?")
//            )
        var actionYes = NotificationCompat.Action.Builder(R.mipmap.gunsiya_round,"예", yesPedingIntent).build()
        var actionNo = NotificationCompat.Action.Builder(R.mipmap.gunsiya_round,"아니오", noPedingIntent).build()
//        nb.addAction(actionYes)
//        nb.addAction(actionNo)
        return nb
    }
}

