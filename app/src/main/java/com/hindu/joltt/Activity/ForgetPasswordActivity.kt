package com.hindu.joltt.Activity

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.R

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var passwordReset_Btn:AppCompatButton
    private lateinit var uid_forgetPassword:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)


        passwordReset_Btn = findViewById(R.id.passwordReset_Btn)
        uid_forgetPassword = findViewById(R.id.uid_forgetPassword)



        passwordReset_Btn.setOnClickListener {
            val uid: String = uid_forgetPassword.text.toString()+"@cuchd.in"
            if (uid.isEmpty()){
                Toast.makeText(this, "Please enter your UID", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(uid)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){
                            Toast.makeText(this,"Password reset mail sent success", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
    private fun resetPassword(){

    }
}