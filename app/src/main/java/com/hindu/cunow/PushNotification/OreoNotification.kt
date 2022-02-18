package com.hindu.cunow.PushNotification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import retrofit2.http.Body

class OreoNotification(base:Context?):ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    companion object
    {
        private const val CHANNEL_ID = "com.hindu.cunow"
        private const val CHANNEL_NAME = "cuNowNotification"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getMnanger!!.createNotificationChannel(channel)
    }
    val getMnanger:NotificationManager? get(){
        if (notificationManager == null){
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }
    @TargetApi(Build.VERSION_CODES.O)
    fun getOreoNotification(title:String?, body: String, pendingIntent:PendingIntent?, soundUri: Uri?, icon:String):Notification.Builder{
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(icon.toInt())
            .setSound(soundUri)
            .setAutoCancel(true)
    }
}