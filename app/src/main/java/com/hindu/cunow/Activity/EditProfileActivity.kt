package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
    }

    private fun retrieveUserData(){
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(FirebaseAuth
                .getInstance()
                .currentUser!!.uid)

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val users = snapshot.getValue(UserModel::class.java)
                    Glide.with(this@EditProfileActivity)
                        .load(users!!.profileImage)
                        .into(editProfileImage)
                    ProfileUserName_editText.setText(users.fullName)
                    ProfileUserName_editText.setText(users.fullName)
                    ProfileUserName_editText.setText(users.fullName)
                    ProfileUserName_editText.setText(users.fullName)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}