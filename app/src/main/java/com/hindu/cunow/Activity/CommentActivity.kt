package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.CommentAdapter
import com.hindu.cunow.Model.CommentModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.post_layout.*

class CommentActivity : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentList:MutableList<CommentModel>? = null
    private var commentsAdapter: CommentAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisher").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.RecyclerViewComment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        commentList = ArrayList()
        commentsAdapter = CommentAdapter(this, commentList as ArrayList<CommentModel>)
        recyclerView.adapter = commentsAdapter
        displayCaption()
        loadComments()
        userInfo()


        addCommentButton.setOnClickListener { view->
            addComment(view)
        }

    }

    private fun addComment(view:View){
        if (addCommentEditText.text.isEmpty()){
            Snackbar.make(view,"please write something..", Snackbar.LENGTH_SHORT).show()
        }else{
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("Comments")
                .child(postId)

            val commentId = dataRef.push().key

            val dataMap = HashMap<String,Any>()
            dataMap["commentId"] = commentId!!
            dataMap["commentText"] = addCommentEditText.text.toString()
            dataMap["commenter"] = firebaseUser!!.uid

            dataRef.push().setValue(dataMap)
            addCommentEditText.text.clear()
            loadComments()
        }
    }

    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(firebaseUser!!.uid)

        usersRef.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        val user = snapshot.getValue(UserModel::class.java)
                        Glide.with(this@CommentActivity).load(user!!.profileImage).into(currentUserProfileComment)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    private fun loadComments(){
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    noCommentsText.visibility = View.GONE
                    RecyclerViewComment.visibility = View.VISIBLE
                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(CommentModel::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentsAdapter!!.notifyDataSetChanged()

                }else{
                    noCommentsText.visibility = View.VISIBLE
                    RecyclerViewComment.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun displayCaption(){
        publisherInfo()
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")
            .child(postId).child("caption")

        postRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val caption = snapshot.value.toString()
                    postCaptionComment.text = caption
                }else if (caption.text == ""){
                    postCaptionComment.text = "No Caption Added"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun publisherInfo(){
        val userDataRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(publisherId)
        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue<UserModel>(UserModel::class.java)
                    Glide.with(this@CommentActivity).load(data!!.profileImage).into(publisherProfileComment)
                    publisherNameComment.text = data.fullName

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}