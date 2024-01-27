package com.hindu.joltt.Activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R

class HelpActivity : AppCompatActivity() {

    private lateinit var Help_Back:ImageView
    private lateinit var send_btn:AppCompatButton
    private lateinit var help_editText:EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        Help_Back = findViewById(R.id.Help_Back)
        send_btn = findViewById(R.id.send_btn)
        help_editText = findViewById(R.id.help_editText)


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