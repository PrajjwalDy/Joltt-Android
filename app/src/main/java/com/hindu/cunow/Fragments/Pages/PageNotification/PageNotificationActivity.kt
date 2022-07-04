package com.hindu.cunow.Fragments.Pages.PageNotification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R

class PageNotificationActivity : AppCompatActivity() {
    private var pageId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_notification)
    }

    private fun loadNotification(){
        val data = FirebaseDatabase.getInstance().reference.child("PageNotification")
            .child("AllNotifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(pageId)
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}