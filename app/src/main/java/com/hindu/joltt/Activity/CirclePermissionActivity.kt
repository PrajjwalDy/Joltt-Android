package com.hindu.joltt.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R

class CirclePermissionActivity : AppCompatActivity() {

    private lateinit var circleId:String
    private lateinit var admin: String
    private var mPermission = ""
    private var private = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_permission)

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()
        admin = intent.getStringExtra("admin").toString()


    }


    private fun setCircleAsPrivate(view:View){
        val data = FirebaseDatabase.getInstance().reference.child("Circle")

        val dataMap = HashMap<String,Any>()
        dataMap["privateC"] = true
        data.child(circleId).updateChildren(dataMap)
        Snackbar.make(view,"Permission Updated", Snackbar.LENGTH_SHORT).show()

    }

    private fun whoCanMessage(view: View){
        val data = FirebaseDatabase.getInstance().reference.child("Circle")

        val dataMap = HashMap<String,Any>()
        dataMap["mPermission"] = true
        data.child(circleId).updateChildren(dataMap)
        Snackbar.make(view,"Permission Updated", Snackbar.LENGTH_SHORT).show()
    }

    private fun setAsPublic(view: View){
        val data = FirebaseDatabase.getInstance().reference.child("Circle")

        val dataMap = HashMap<String,Any>()
        dataMap["privateC"] = false
        data.child(circleId).updateChildren(dataMap)
        Snackbar.make(view,"Permission Updated", Snackbar.LENGTH_SHORT).show()
    }

    private fun onlyMe(view: View){
        val data = FirebaseDatabase.getInstance().reference.child("Circle")

        val dataMap = HashMap<String,Any>()
        dataMap["mPermission"] = false
        data.child(circleId).updateChildren(dataMap)
        Snackbar.make(view,"Permission Updated", Snackbar.LENGTH_SHORT).show()
    }
}
