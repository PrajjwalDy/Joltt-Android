package com.hindu.cunow.datasource

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.NotificationModel

class FirebaseDataSource {
    private val database = FirebaseDatabase.getInstance().reference

    private var currentPage = 0

    fun getNextPage(callback:(List<NotificationModel>)->Unit){
        val query = database.child("Notification")
            .child("AllNotification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .orderByChild("notificationId")
            .limitToLast(PAGE_SIZE)
            .startAt(currentPage*PAGE_SIZE.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newData = snapshot.children.mapNotNull{it.getValue(NotificationModel::class.java)}
                currentPage++
                callback(newData)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}