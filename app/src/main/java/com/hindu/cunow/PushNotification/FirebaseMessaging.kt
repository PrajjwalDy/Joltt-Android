package com.hindu.cunow.PushNotification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hindu.cunow.MainActivity

class FirebaseMessaging:FirebaseMessagingService()
{

    override fun onMessageReceived(mRemoteNotification: RemoteMessage) {
        super.onMessageReceived(mRemoteNotification)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null){

                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                    sendOreoNotification(mRemoteNotification)
                }else{
                    sendNotification(mRemoteNotification)
            }
        }
    }

    private fun sendNotification(mRemoteNotification: RemoteMessage) {
        val user = mRemoteNotification.data["user"]
        val icon = mRemoteNotification.data["icon"]
        val title = mRemoteNotification.data["title"]
        val body = mRemoteNotification.data["body"]

        val notification = mRemoteNotification.notification
        val j = user!!.toInt()
        val intent =Intent(this,MainActivity::class.java)

        val bundle = Bundle()
        bundle.putString("uid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            j,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)

            .setSmallIcon(icon!!.toInt())
            .setContentText(body)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val i = 0
        if (i>0){
            noti.notify(i, builder.build())
        }
    }

    private fun sendOreoNotification(mRemoteNotification: RemoteMessage) {
        val user = mRemoteNotification.data["user"]
        val icon = mRemoteNotification.data["icon"]
        val title = mRemoteNotification.data["title"]
        val body = mRemoteNotification.data["body"]

        val notification = mRemoteNotification.notification
        val j = user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent =Intent(this,MainActivity::class.java)

        val bundle = Bundle()
        bundle.putString("uid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)

        val build: Notification.Builder = oreoNotification.getOreoNotification(title,body!!,pendingIntent,defaultSound,icon!!)

        val i = 0
        if (i>0){
            oreoNotification.getMnanger!!.notify(i,build.build())
        }

    }
}