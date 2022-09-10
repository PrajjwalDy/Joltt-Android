package com.hindu.cunow.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.MainActivity
import com.hindu.cunow.Model.FacultyData
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_faculty_login.*
import kotlinx.android.synthetic.main.activity_faculty_signup.*
import kotlinx.android.synthetic.main.activity_log_in.*

class FacultyLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_login)

        faculty_login_button.setOnClickListener {
            logIn()
        }

        faculty_cna_button.setOnClickListener{
            val intent = Intent(this, FacultySignup::class.java)
            startActivity(intent)
        }

    }

    private fun logIn(){
        val email = faculty_email_login.text.toString().trim{it <=' '}
        val password = faculty_password_login.text.toString().trim{it <=' '}

        when{
            TextUtils.isEmpty(faculty_email_login.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@FacultyLogin, "Please enter your email", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(faculty_password_login.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@FacultyLogin, "Please enter your password", Toast.LENGTH_SHORT).show()
            }

            else->{
                val progressDialog = ProgressDialog(this@FacultyLogin)
                progressDialog.setMessage("Signing In")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener{task->
                        if(task.isSuccessful){
                            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
                                if (user!!.isEmailVerified) {
                                    progressDialog.dismiss()
                                    checkStatus()
                                } else{
                                    progressDialog.dismiss()
                                    val intent = Intent(this@FacultyLogin,FacultyVerificationActivity::class.java)
                                    startActivity(intent)
                                }
                        }

                        }else{
                            val message = task.exception.toString()

                            Toast.makeText(this@FacultyLogin, message,Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
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