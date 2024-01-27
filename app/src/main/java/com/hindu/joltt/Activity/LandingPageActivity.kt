package com.hindu.joltt.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hindu.cunow.R

class LandingPageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser


//        if(FirebaseAuth.getInstance().currentUser != null){
//            mainLL.visibility = View.GONE
//            eye_LL.visibility = View.VISIBLE
//            checkUser(user)
//        }else{
//            mainLL.visibility = View.VISIBLE
//            eye_LL.visibility = View.GONE
//        }
//
//        landing_faculty_button.setOnClickListener {
//            val intent = Intent(this, FacultyLogin::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        landing_student_button.setOnClickListener {
//            val intent = Intent(this, LogInActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }



//    private fun checkUser(user : FirebaseUser?){
//        val progressDialog = Dialog(this)
//        progressDialog.setContentView(R.layout.profile_dropdown_menu)
//        progressDialog.show()
//        mainLL.visibility = View.GONE
//
//        val userData = FirebaseDatabase.getInstance()
//            .reference.child("Users")
//            .child(FirebaseAuth.getInstance().currentUser!!.uid)
//
//        userData.addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                    if (user!!.isEmailVerified){
//                        val intent = Intent(this@LandingPageActivity, MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
//                        finish()
//                }
//                progressDialog.dismiss()
//                mainLL.visibility = View.VISIBLE
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//
//
//        })
//
//    }
}