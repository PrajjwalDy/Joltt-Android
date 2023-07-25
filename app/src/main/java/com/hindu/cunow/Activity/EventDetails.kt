package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Fragments.Event.EventViewModel
import com.hindu.cunow.Model.EventModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_event_details.*

class EventDetails : AppCompatActivity() {
    private var eventId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val intent = intent
        eventId = intent.getStringExtra("eventId").toString()
        getDetails()
    }

    private fun getDetails(){
        val db = FirebaseDatabase.getInstance().reference
            .child("Events").child(eventId)
        db.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(EventModel::class.java)
                    Glide.with(this@EventDetails).load(data!!.eventImg).into(eventImage_details)
                   /* eventName_details.text = data.eventName.toString()
                    eventDescription_details.text = data.eventDescription.toString()
                    startDate.text = data.startDate.toString()
                    endDate.text = data.endDate.toString()*/
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}