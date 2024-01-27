package com.hindu.joltt.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class LogInActivity : AppCompatActivity() {

    private lateinit var login_btn:AppCompatButton
    private lateinit var createAccount_text:TextView
    private lateinit var forget_password_txt:TextView
    private lateinit var uid_edit_text:EditText
    private lateinit var password_edit_text:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        login_btn = findViewById(R.id.login_btn)
        createAccount_text = findViewById(R.id.createAccount_text)
        forget_password_txt = findViewById(R.id.forget_password_txt)
        uid_edit_text = findViewById(R.id.uid_edit_text)
        password_edit_text = findViewById(R.id.password_edit_text)



        login_btn.setOnClickListener {
            login()
        }

        createAccount_text.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }

        forget_password_txt.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)

        }
    }

    private fun login() {
        val email = uid_edit_text.text.toString() //+"@cuchd.in"
        val password = password_edit_text.text.toString()

        when {
            TextUtils.isEmpty(uid_edit_text.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this@LogInActivity, "Please enter your UID", Toast.LENGTH_SHORT)
                    .show()
            }

            TextUtils.isEmpty(password_edit_text.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this@LogInActivity, "Please enter your Password", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                val progressDialog = ProgressDialog(this@LogInActivity)
                progressDialog.setMessage("Diving In")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            FirebaseAuth.getInstance().currentUser?.reload()
                                ?.addOnCompleteListener { task ->
                                    if (user!!.isEmailVerified) {
                                        progressDialog.dismiss()
                                        val intent =
                                            Intent(this@LogInActivity, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        Toast.makeText(
                                            this@LogInActivity,
                                            "LogIn Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        progressDialog.dismiss()
                                        val intent =
                                            Intent(this@LogInActivity, VerifyActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                        } else {
                            val message = task.exception.toString()

                            Toast.makeText(this@LogInActivity, message, Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if (user!!.isEmailVerified) {
                checkData()
            }
        }
    }

    private fun checkData(){
        val dbRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        dbRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("college")
                    ||snapshot.hasChild("course")
                    ||snapshot.hasChild("branch")
                    ||snapshot.hasChild("skills")
                    ||snapshot.hasChild("experience")
                    ||snapshot.hasChild("place")){

                    val intent = Intent(this@LogInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this@LogInActivity, UserDetailsActivity::class.java)
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