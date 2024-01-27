package com.hindu.joltt.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.R
import java.util.Locale

class FacultySignup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_signup)

    }



    private fun saveData(facultyName: String, facultyEmail: String,
                         facultyPhone: String, EID:String, password: String,
                         progressDialog: ProgressDialog) {
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
                        saveDataUser(facultyName,facultyEmail,facultyPhone,EID,password,progressDialog)
                    }
            }
        }
    }

    private fun saveDataUser(facultyName: String, facultyEmail: String,
                             facultyPhone: String, EID:String, password: String,
                             progressDialog: ProgressDialog){

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val userRef = database.reference.child("Users")

        val dataMap = HashMap<String,Any>()
        dataMap["uid"] = currentUserID
        dataMap["fullName"] = facultyName
        dataMap["ID"] = facultyEmail
        dataMap["password"] = password
        dataMap["phone"] = facultyPhone
        dataMap["profileImage"] = "https://developers.google.cn/web/images/contributors/no-photo.jpg"
        dataMap["verification"] = false
        dataMap["firstVisit"] = true
        dataMap["searchName"] = facultyName.toString().lowercase(Locale.getDefault())
        dataMap["private"] = false
        dataMap["firstVisit"] = true
        dataMap["faculty"] = true
        dataMap["confessionVisited"] = true

        userRef.child(currentUserID).setValue(dataMap).addOnCompleteListener{task->
            if (task.isSuccessful){
                progressDialog.dismiss()

                firebaseUser!!.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(firebaseUser.uid)
                        .setValue(true)
                }
            }
        }

    }
}