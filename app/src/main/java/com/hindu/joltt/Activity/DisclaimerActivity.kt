package com.hindu.joltt.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.hindu.cunow.R

class DisclaimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)

        val backbtn = findViewById<ImageView>(R.id.backBtnDis)

        backbtn.setOnClickListener{
            finish()
        }

    }
}