package com.hindu.joltt.Activity

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R

class UserSupportActivity : AppCompatActivity() {
    private var profileId = ""
    private lateinit var firebaseUser: FirebaseUser

    var quote = ""

    private lateinit var supportQ1:CardView
    private lateinit var supportQ2:CardView
    private lateinit var supportQ3:CardView

    private lateinit var q1_text:TextView
    private lateinit var q2_text:TextView
    private lateinit var q3_text:TextView

    private lateinit var reportBlockUser:AppCompatButton
    private lateinit var reportUser:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_support)


        supportQ1 = findViewById(R.id.supportQ1)
        supportQ2 = findViewById(R.id.supportQ2)
        supportQ3 = findViewById(R.id.supportQ3)

        q1_text = findViewById(R.id.q1_text)
        q2_text = findViewById(R.id.q2_text)
        q3_text = findViewById(R.id.q3_text)

        reportBlockUser = findViewById(R.id.reportBlockUser)
        reportUser = findViewById(R.id.reportUser)



        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val intent = intent
        profileId = intent.getStringExtra("uid").toString()

        supportQ1.setOnClickListener {
            supportQ1.setCardBackgroundColor(Color.parseColor("#009eff"))
            supportQ2.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ3.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            q1_text.setTextColor(resources.getColor(R.color.white))
            q2_text.setTextColor(resources.getColor(R.color.red))
            q3_text.setTextColor(resources.getColor(R.color.red))

            quote = "1"

        }

        supportQ2.setOnClickListener {
            supportQ1.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ2.setCardBackgroundColor(Color.parseColor("#009eff"))
            supportQ3.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            q1_text.setTextColor(resources.getColor(R.color.red))
            q2_text.setTextColor(resources.getColor(R.color.white))
            q3_text.setTextColor(resources.getColor(R.color.red))
            quote = "2"
        }

        supportQ3.setOnClickListener {
            supportQ1.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ2.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ3.setCardBackgroundColor(Color.parseColor("#009eff"))
            q1_text.setTextColor(resources.getColor(R.color.red))
            q2_text.setTextColor(resources.getColor(R.color.red))
            q3_text.setTextColor(resources.getColor(R.color.white))
            quote = "3"
        }

        reportUser.setOnClickListener {
            report()
        }
        reportBlockUser.setOnClickListener {
            reportAndBlock()
        }

    }

    private fun report() {
        val dataRef = FirebaseDatabase.getInstance().reference
            .child("UserReports")

        val commentId = dataRef.push().key

        val dataMap = HashMap<String, Any>()
        dataMap["reportId"] = commentId!!
        when (quote) {
            "1" -> {
                dataMap["report"] = "This is a Fake Account"
            }
            "2" -> {
                dataMap["report"] = "It's posting content that shouldn't be on CUNow"
            }
            "3" -> {
                dataMap["report"] = "Person does not belong to Chandigarh University"
            }
            else -> {
                dataMap["report"] = "Empty"
            }
        }

        dataMap["reporter"] = FirebaseAuth.getInstance().currentUser!!.uid
        dataMap["rUserId"] = profileId


        dataRef.child(commentId)
            .updateChildren(dataMap)
        Toast.makeText(this, "User reported successfully", Toast.LENGTH_SHORT).show()
        finish()
    }


    private fun reportAndBlock(){
        report()
        firebaseUser.uid.let { it1->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following").child(profileId)
                .removeValue()
        }

        firebaseUser.uid.let { it1->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers").child(it1.toString()
                )
                .removeValue()
        }
        block()
        finish()
    }

    private fun block(){
         FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(firebaseUser.uid)
            .child("BlockedUsers")
            .child(profileId)
            .setValue(true)

        finish()
    }
}
