package com.hindu.joltt.Activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R

class FeedbackActivity : AppCompatActivity() {


    private lateinit var feedbackBack:ImageView
    private lateinit var feedbackTxt:TextView
    private lateinit var sendFeedback:AppCompatButton

    private lateinit var fName:EditText
    private lateinit var feedText:EditText
    private lateinit var ll_feedLayout:LinearLayout
    private lateinit var done:LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)


        feedbackBack = findViewById(R.id.feedbackBack)
        feedbackTxt = findViewById(R.id.feedbackTxt)
        sendFeedback = findViewById(R.id.sendFeedback)
        fName = findViewById(R.id.fName)
        feedText = findViewById(R.id.feedText)
        ll_feedLayout = findViewById(R.id.ll_feedLayout)
        done = findViewById(R.id.done)







        feedbackBack.setOnClickListener {
            finish()
        }
        feedbackTxt.setOnClickListener {
            finish()
        }

        sendFeedback.setOnClickListener {
            requestHelp()
        }
    }
    private fun requestHelp(){
        if(fName.text.isEmpty()||feedText.text.isEmpty()){
            Toast.makeText(this,"Credentials are Required", Toast.LENGTH_LONG).show()
        }else{
            val ref = FirebaseDatabase.getInstance().reference.child("Feedback")
            val helpId = ref.push().key

            val postMap = HashMap<String,Any>()
            postMap["feedbackId"] = helpId!!
            postMap["fName"] = fName.text.toString()
            postMap["feedText"] = feedText.text.toString()
            postMap["fUID"] = FirebaseAuth.getInstance().currentUser!!.uid

            ref.child(helpId).updateChildren(postMap)

            fName.text.clear()
            feedText.text.clear()
            Toast.makeText(this,"Feedback sent success", Toast.LENGTH_LONG).show()
            ll_feedLayout.visibility  = View.GONE
            done.visibility = View.VISIBLE

            finish()

        }

    }
}