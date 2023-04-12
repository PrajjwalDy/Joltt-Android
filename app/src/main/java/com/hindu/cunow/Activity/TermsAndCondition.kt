package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_terms_and_condition.*

class TermsAndCondition : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)
        TermandCondition_Back.setOnClickListener{
            finish()
    }}}


