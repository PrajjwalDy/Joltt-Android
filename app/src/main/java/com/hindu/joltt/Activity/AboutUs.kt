package com.hindu.joltt.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.hindu.cunow.R

class AboutUs : AppCompatActivity() {

    private lateinit var aboutBack:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        aboutBack = findViewById(R.id.About_Back)
      aboutBack.setOnClickListener {
          finish()
      }

    }
}