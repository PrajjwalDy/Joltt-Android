package com.hindu.joltt.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_internships_details.*

class internshipsDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internships_details)

        internshipApply.setOnClickListener{
            val open = Intent(android.content.Intent.ACTION_VIEW)

        }
    }
}