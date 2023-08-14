package com.hindu.joltt.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.MainActivity
import kotlinx.android.synthetic.main.activity_landing_page.eye_LL
import kotlinx.android.synthetic.main.activity_landing_page.landing_faculty_button
import kotlinx.android.synthetic.main.activity_landing_page.landing_student_button
import kotlinx.android.synthetic.main.activity_landing_page.mainLL

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if(FirebaseAuth.getInstance().currentUser != null){
            mainLL.visibility = View.GONE
            eye_LL.visibility = View.VISIBLE
            checkUser(user)
        }else{
            mainLL.visibility = View.VISIBLE
            eye_LL.visibility = View.GONE
        }

        landing_faculty_button.setOnClickListener {
            val intent = Intent(this, FacultyLogin::class.java)
            startActivity(intent)
            finish()
        }

        landing_student_button.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if(FirebaseAuth.getInstance().currentUser != null){
            mainLL.visibility = View.GONE
            eye_LL.visibility = View.VISIBLE
            checkUser(user)
        }else{
            mainLL.visibility = View.VISIBLE
            eye_LL.visibility = View.GONE
        }
    }

    private fun checkUser(user : FirebaseUser?){
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        mainLL.visibility = View.GONE

        val userData = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        userData.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    if (user!!.isEmailVerified){
                        val intent = Intent(this@LandingPageActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                }
                progressDialog.dismiss()
                mainLL.visibility = View.VISIBLE
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }



        })

    }
}