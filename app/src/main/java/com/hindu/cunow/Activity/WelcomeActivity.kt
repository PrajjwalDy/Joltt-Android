package com.hindu.cunow.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_landing_page.*
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        welcome_signin.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        register_welcome.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
    override fun onStart() {
        super.onStart()

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (FirebaseAuth.getInstance().currentUser != null){

            if (user!!.isEmailVerified){
                val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
        }
    }

}