package com.hindu.joltt.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hindu.cunow.R

class SettingActivity : AppCompatActivity() {

    private lateinit var Setting_Back:ImageView
    private lateinit var logout:LinearLayout
    private lateinit var edit_profile:LinearLayout
    private lateinit var help:LinearLayout
    private lateinit var about_us:LinearLayout
    private lateinit var terms_condition:LinearLayout
    private lateinit var verify:LinearLayout
    private lateinit var privacy_settings:LinearLayout
    private lateinit var webscraping:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        Setting_Back = findViewById(R.id.Setting_Back)
        logout = findViewById(R.id.logout)
        edit_profile = findViewById(R.id.edit_profile)
        help = findViewById(R.id.help)
        about_us = findViewById(R.id.about_us)
        terms_condition = findViewById(R.id.terms_condition)
        verify = findViewById(R.id.verify)
        privacy_settings = findViewById(R.id.privacy_settings)
        webscraping = findViewById(R.id.webscraping)




        Setting_Back.setOnClickListener {
            finish()
        }

        about_us.setOnClickListener {
            val intent = Intent(this, com.hindu.joltt.Activity.AboutUs::class.java)
            startActivity(intent)

            }



        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@SettingActivity,WelcomeActivity::class.java)
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

        privacy_settings.setOnClickListener {
            val intent = Intent(this,   PrivacySettingsActivity::class.java)
            startActivity(intent)
        }

        webscraping.setOnClickListener {
            startActivity(Intent(this,UserDetailsActivity::class.java))
        }
    }
}