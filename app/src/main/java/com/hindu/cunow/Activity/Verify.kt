package com.hindu.cunow.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R

class Verify : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify2)

        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (firebaseUser!!.isEmailVerified){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}