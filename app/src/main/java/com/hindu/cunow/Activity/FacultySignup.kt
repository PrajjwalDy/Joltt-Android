package com.hindu.cunow.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_faculty_signup.*

class FacultySignup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_signup)

        faculty_signup_button.setOnClickListener {
            faculty_signup()
        }

        faculty_cna_login_button.setOnClickListener {
            val intent = Intent(this, FacultyLogin::class.java)
            startActivity(intent)
        }
    }

    private fun faculty_signup(){
        val facultyName = faculty_name.text.toString().trim{it <=' '}
        val facultyEmail = faculty_email.text.toString().trim{it <=' '}
        val facultyPhone = faculty_phone.text.toString().trim{it <=' '}
        val EID = faculty_eid.text.toString().trim(){it <=' '}
        val password = faculty_password.text.toString().trim{it <=' '}

        when{
            TextUtils.isEmpty(faculty_name.text.toString().trim{it <=' '})->{
                Toast.makeText(this@FacultySignup, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(faculty_email.text.toString().trim{it <=' '})->{
                Toast.makeText(this@FacultySignup, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(faculty_phone.text.toString().trim{it <=' '})->{
                Toast.makeText(this@FacultySignup, "Please enter your phone", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(faculty_eid.text.toString().trim(){it <=' '})->{
                Toast.makeText(this@FacultySignup, "Please enter your EID", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(faculty_password.text.trim{it <=' '})->{
                Toast.makeText(this@FacultySignup, "Please enter your password", Toast.LENGTH_SHORT).show()
            }
            else->{
                val progressDialog = ProgressDialog(this@FacultySignup)
                progressDialog.setTitle("SigningUp")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(facultyEmail,password)
                    .addOnCompleteListener{task->
                        if(task.isSuccessful){
                            saveData(facultyName,facultyEmail,facultyPhone,EID,password,progressDialog)
                        }else{
                            val message = task.exception.toString()
                            Toast.makeText(this, "Some Error occurred:$message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveData(facultyName: String, facultyEmail: String, facultyPhone: String, EID:String, password: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val userRef = database.reference.child("Faculty")

        val dataMap = HashMap<String,Any>()
        dataMap["F_UID"] = currentUserID
        dataMap["F_Name"] = facultyName
        dataMap["F_EID"] = EID
        dataMap["F_Phone"] = facultyPhone
        dataMap["F_Email"] = facultyEmail
        dataMap["F_Password"] = password
        dataMap["F_Verified"] = false
        dataMap["Faculty_Y"] = true

        userRef.child(currentUserID).setValue(dataMap).addOnCompleteListener{task->
            if(task.isSuccessful){
                progressDialog.dismiss()

                firebaseUser!!.sendEmailVerification()
                    .addOnCompleteListener{task->
                        Toast.makeText(this, "Verification link send success", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@FacultySignup, FacultyVerificationActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        firebaseUser.uid.let { it1 ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it1.toString())
                                .child("Following").child(firebaseUser.uid)
                                .setValue(true)
                        }
                    }
            }
        }
    }
}