package com.hindu.joltt.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Model.CircleModel
import kotlinx.android.synthetic.main.activity_circle_permission.everyoneCanSend
import kotlinx.android.synthetic.main.activity_circle_permission.onlyMeCanSend
import kotlinx.android.synthetic.main.activity_circle_permission.setAsPublicCircle
import kotlinx.android.synthetic.main.activity_circle_permission.setPrivateCircle

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

        setPrivateCircle.setOnClickListener {view->
            setCircleAsPrivate(view)
        }

        setAsPublicCircle.setOnClickListener { view->
            setAsPublic(view)
        }

        onlyMeCanSend.setOnClickListener { view->
            onlyMe(view)
        }

        everyoneCanSend.setOnClickListener { view->
            whoCanMessage(view)
        }



        checkPermission()

    }

    private fun checkPermission(){
        val data = FirebaseDatabase.getInstance().reference.child("Circle")
            .child(circleId)
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val circleData = snapshot.getValue(CircleModel::class.java)
                    if (circleData!!.privateC){
                        setPrivateCircle.setBackgroundColor(resources.getColor(R.color.red))
                        setPrivateCircle.setTextColor(resources.getColor(R.color.white))
                        setAsPublicCircle.setBackgroundColor(resources.getColor(R.color.white))
                        setAsPublicCircle.setTextColor(resources.getColor(R.color.red))
                    }else{
                        setPrivateCircle.setBackgroundColor(resources.getColor(R.color.white))
                        setPrivateCircle.setTextColor(resources.getColor(R.color.red))
                        setAsPublicCircle.setBackgroundColor(resources.getColor(R.color.red))
                        setAsPublicCircle.setTextColor(resources.getColor(R.color.white))
                    }

                    if (circleData.mPermission){
                        onlyMeCanSend.setBackgroundColor(resources.getColor(R.color.white))
                        onlyMeCanSend.setTextColor(resources.getColor(R.color.red))
                        everyoneCanSend.setBackgroundColor(resources.getColor(R.color.red))
                        everyoneCanSend.setTextColor(resources.getColor(R.color.white))
                    }else{
                        onlyMeCanSend.setBackgroundColor(resources.getColor(R.color.red))
                        onlyMeCanSend.setTextColor(resources.getColor(R.color.white))
                        everyoneCanSend.setBackgroundColor(resources.getColor(R.color.white))
                        everyoneCanSend.setTextColor(resources.getColor(R.color.red))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

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
