package com.hindu.cunow.Activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_view_full.*
import kotlinx.android.synthetic.main.fragment_user_profiel.*

class ViewFullActivity : AppCompatActivity() {
    private var postId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full)

        postId = intent.getStringExtra("postId").toString()

        userInfo()
    }


    private fun userInfo(){

        val userRef = FirebaseDatabase.getInstance().reference.child("Post").child(postId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userData = snapshot.getValue(PostModel::class.java)
                    Glide.with(this@ViewFullActivity).load(userData!!.image).into(postImage_full)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("some error occurred")
            }
        })
    }

}