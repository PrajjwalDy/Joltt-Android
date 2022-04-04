package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_circle_details.*
import kotlinx.android.synthetic.main.activity_circle_details.view.*

class CircleDetailsActivity : AppCompatActivity() {

    private lateinit var circleId:String
    private lateinit var admin: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_details)

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()
        admin = intent.getStringExtra("admin").toString()


        getCircleDetails()
        getAdmin()
    }

    private fun getCircleDetails(){
        val dataRef = FirebaseDatabase.getInstance().reference.child("Circle").child(circleId)

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(CircleModel::class.java)
                    Glide.with(this@CircleDetailsActivity).load(data!!.icon).into(circleIcon_details)
                    circle_name_details.text = data.circleName
                    circle_description_details.text = data.circle_description
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getAdmin(){

        val userData = FirebaseDatabase.getInstance().reference.child("Users").child(admin)
        userData.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    Glide.with(this@CircleDetailsActivity).load(user!!.profileImage).into(profileImage_circleAdmin)
                    fullName_admin.text = user.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}