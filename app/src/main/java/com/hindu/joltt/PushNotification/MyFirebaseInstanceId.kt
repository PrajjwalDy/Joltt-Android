package com.hindu.joltt.PushNotification

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.hindu.joltt.Model.Token

class MyFirebaseInstanceId:FirebaseMessagingService() {
  override fun onNewToken(p0:String){
      super.onNewToken(p0)

      val firebaseUser = FirebaseAuth.getInstance().currentUser
      val refreshToken = FirebaseMessaging.getInstance().token.toString()

      if (firebaseUser != null){
          updateToken(refreshToken)
      }
  }

    private fun updateToken(refreshToken: String){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
            if (!task.isSuccessful){
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val newToken = Token(refreshToken)
            FirebaseDatabase.getInstance().reference.child("Tokens")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(newToken)
        })

    }

}