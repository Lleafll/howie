package com.lorenz.howie.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationChannelId = "com.lorenz.howie"
        createNotificationChannel(notificationChannelId)
        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("TITLE")
            .setContentText("CONTENT")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId = 0
            notify(notificationId, builder.build())
        }
        return START_STICKY
    }
}

private fun NotificationService.createNotificationChannel(notificationChannelId: String) {
    val channel = NotificationChannel(
        notificationChannelId,
        "Howie Notification",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}