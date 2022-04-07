package com.hindu.cunow.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hindu.cunow.MainActivity
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.uid_edit_text
import kotlinx.android.synthetic.main.activity_sign_up.*

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        login_btn.setOnClickListener {
                login()
        }

        createAccount_text.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }
    }

    private fun login(){
        val email = uid_edit_text.text.toString()+"@cuchd.in"
        val password = password_edit_text.text.toString()

        when{
            TextUtils.isEmpty(uid_edit_text.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@LogInActivity, "Please enter your UID", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password_edit_text.text.toString().trim{ it <= ' '})->{
                Toast.makeText(this@LogInActivity, "Please enter your Password", Toast.LENGTH_SHORT).show()
            }

            else->{
                val progressDialog = ProgressDialog(this@LogInActivity)
                progressDialog.setMessage("Diving In")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
                                if (user!!.isEmailVerified) {
                                    progressDialog.dismiss()
                                    val intent = Intent(this@LogInActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    Toast.makeText(this@LogInActivity, "LogIn Success", Toast.LENGTH_SHORT).show()
                                } else{
                                    progressDialog.dismiss()
                                    val intent = Intent(this@LogInActivity,VerifyActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }else{
                            val message = task.exception.toString()

                            Toast.makeText(this@LogInActivity, message,Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }

            }
        }
    }

    override fun onStart() {
        super.onStart()
val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser

            if (FirebaseAuth.getInstance().currentUser != null){

                if (user!!.isEmailVerified){
                    val intent = Intent(this@LogInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                }
        }

    }
}