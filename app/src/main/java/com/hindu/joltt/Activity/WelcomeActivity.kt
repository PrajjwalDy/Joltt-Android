package com.hindu.joltt.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.MainActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcome_signin:AppCompatButton
    private lateinit var register_welcome:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        welcome_signin = findViewById(R.id.welcome_signin)
        register_welcome = findViewById(R.id.register_welcome)


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
                checkData()
            }
        }
    }

    private fun checkData(){
        val dbRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("college")
                    ||snapshot.hasChild("course")
                    ||snapshot.hasChild("branch")
                    ||snapshot.hasChild("skills")
                    ||snapshot.hasChild("experience")
                    ||snapshot.hasChild("place")){

                    val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this@WelcomeActivity, UserDetailsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        dbRef.keepSynced(true)
    }

}