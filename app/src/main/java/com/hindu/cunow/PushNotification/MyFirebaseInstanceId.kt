package com.hindu.cunow.PushNotification

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseInstanceId:FirebaseMessagingService() {
  override fun onNewToken(p0:String){
      super.onNewToken(p0)

      val firebaseUser = FirebaseAuth.getInstance().currentUser
      val refreshToken = FirebaseMessaging.getInstance().token.toString()

      if (firebaseUser != null){
          updateToken(refreshToken)
      }
  }

    private fun updateToken(refreshToken: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token = Token(refreshToken)
        ref.child(firebaseUser!!.uid).setValue(token)
    }

}