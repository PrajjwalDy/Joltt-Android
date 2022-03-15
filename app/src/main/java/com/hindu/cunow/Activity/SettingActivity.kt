package com.hindu.cunow.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        about_us.setOnClickListener {
            val intent = Intent(this,AboutUs::class.java)
            startActivity(intent)
        }


        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@SettingActivity,LogInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        edit_profile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        help.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)

        }

        terms_condition.setOnClickListener {
            val intent = Intent(this, TermsAndCondition::class.java)
            startActivity(intent)
        }

        verify.setOnClickListener {
            val intent = Intent(this,   VerificationRequest::class.java)
            startActivity(intent)
        }


    }
}