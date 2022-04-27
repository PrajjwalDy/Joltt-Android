package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

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