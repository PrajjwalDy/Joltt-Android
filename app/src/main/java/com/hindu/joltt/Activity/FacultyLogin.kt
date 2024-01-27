package com.hindu.joltt.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.MainActivity
import com.hindu.joltt.Model.FacultyData

class FacultyLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_login)

    }



    private fun checkStatus(){
        val data = FirebaseDatabase.getInstance()
            .reference
            .child("Faculty")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val faculty = snapshot.getValue(FacultyData::class.java)
                if (faculty!!.F_Verified!!){
                    val intent = Intent(this@FacultyLogin, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Toast.makeText(this@FacultyLogin, "LogIn Success", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@FacultyLogin, "Account isn't verified yet!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@FacultyLogin, FacultyVerificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FacultyLogin, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }
}