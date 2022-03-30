package com.hindu.cunow.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_user_support.*

class UserSupportActivity : AppCompatActivity() {
    private var profileId = ""
    private lateinit var firebaseUser: FirebaseUser

    var quote = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_support)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val intent = intent
        profileId = intent.getStringExtra("uid").toString()

        supportQ1.setOnClickListener {
            supportQ1.setCardBackgroundColor(Color.parseColor("#009eff"))
            supportQ2.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ3.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            quote = "1"

        }

        supportQ2.setOnClickListener {
            supportQ1.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ2.setCardBackgroundColor(Color.parseColor("#009eff"))
            supportQ3.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            quote = "2"
        }

        supportQ3.setOnClickListener {
            supportQ1.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ2.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
            supportQ3.setCardBackgroundColor(Color.parseColor("#009eff"))
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
    }

    private fun block(){
         FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(firebaseUser.uid)
            .child("BlockedUsers")
            .child(profileId)
            .setValue(true)
    }

}
