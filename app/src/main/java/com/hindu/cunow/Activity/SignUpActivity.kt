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
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        logInAccount_text.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        signUp_button.setOnClickListener {
            signUp()
        }

        tnc_SignUP.setOnClickListener {
            val intent = Intent(this,TermsAndCondition::class.java)
            startActivity(intent)
        }

    }


    private fun signUp() {
        val fullName = fullName_edit_text.text.toString().trim{ it <= ' '}
        val uid = uid_edit_text.text.toString()
        val phone = phone_edit_text.text.toString().trim{ it <= ' '}
        val password = password_Create_AC.text.toString()

        when{
            TextUtils.isEmpty(fullName_edit_text.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@SignUpActivity, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(uid_edit_text.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@SignUpActivity, "Please enter your UID", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(phone_edit_text.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@SignUpActivity, "Please enter your Phone Number", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password_Create_AC.text.toString())->{
                Toast.makeText(this@SignUpActivity, "Please enter your password", Toast.LENGTH_SHORT).show()
            }

            else ->{
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("Registration in progress")
                progressDialog.setTitle("Please wait")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(uid,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            saveData(fullName,uid, phone, password, progressDialog)
                        }else{
                            val message = task.exception.toString()
                            Toast.makeText(this, "Some Error Occurred: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }

            }
        }
    }
    private fun saveData(fullName: String, uid: String, phone: String, password: String,progressDialog:ProgressDialog) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val database = Firebase.database
        val userRef = database.reference.child("Users")

        val dataMap = HashMap<String,Any>()
        dataMap["uid"] = currentUserID
        dataMap["fullName"] = fullName
        dataMap["ID"] = uid
        dataMap["password"] = password
        dataMap["phone"] = phone
        dataMap["profileImage"] = "https://developers.google.cn/web/images/contributors/no-photo.jpg"
        dataMap["verification"] = false
        dataMap["firstVisit"] = true
        dataMap["searchName"] = fullName.toString().lowercase(Locale.getDefault())
        dataMap["private"] = false
        dataMap["firstVisit"] = true
        dataMap["confessionVisited"] = true

        userRef.child(currentUserID).setValue(dataMap).addOnCompleteListener { task->
            if (task.isSuccessful){
                progressDialog.dismiss()

                firebaseUser!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this, "Verification link send success", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignUpActivity, VerifyActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            firebaseUser.uid.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(it1.toString())
                                    .child("Following").child(firebaseUser.uid)
                                    .setValue(true)
                            }
                        }else{
                            Toast.makeText(this, "Some Error occurred or you may entered wrong UID", Toast.LENGTH_LONG).show()
                        }
                    }



            }else{
                val message = task.exception.toString()
                Toast.makeText(this, "Some Error Occurred: $message", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }

        }

    }

}