package com.hindu.joltt.Activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.hindu.cunow.R

class TermsAndCondition : AppCompatActivity() {
    private lateinit var TermandCondition_Back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)


        TermandCondition_Back = findViewById(R.id.TermandCondition_Back)

        TermandCondition_Back.setOnClickListener {
            finish()
        }
    }
}


