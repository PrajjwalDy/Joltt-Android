package com.hindu.joltt.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_verify.proceed_to_verification

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        startActivity()
    }

    private fun startActivity(){
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val user = FirebaseAuth.getInstance().currentUser

        proceed_to_verification.setOnClickListener {view->

            FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task->
                if (firebaseUser!!.isEmailVerified){
                    val intent = Intent(this@VerifyActivity, UserDetailsActivity::class.java)
                    startActivity(intent)
                }else{
                    Snackbar.make(this,view,"Email isn't verified yet!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}