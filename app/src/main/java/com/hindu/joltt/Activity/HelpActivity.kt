package com.hindu.joltt.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.fragment_home.*

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        Help_Back.setOnClickListener {
            finish()
        }

        send_btn.setOnClickListener {
            requestHelp()
        }

    }

    private fun requestHelp(){
        if(help_editText.text.isEmpty()){
            Toast.makeText(this,"Enter your query",Toast.LENGTH_LONG).show()
        }else{
            val ref = FirebaseDatabase.getInstance().reference.child("Help")
            val helpId = ref.push().key

            val postMap = HashMap<String,Any>()
            postMap["helpId"] = helpId!!
            postMap["helpDescription"] = help_editText.text.toString()
            postMap["helpReq"] = FirebaseAuth.getInstance().currentUser!!.uid

            ref.child(helpId).updateChildren(postMap)

            help_editText.text.clear()
            Toast.makeText(this,"Help sent success",Toast.LENGTH_LONG).show()
        }

    }

}