package com.hindu.joltt.Fragments.ConfessionRoom

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.ConfessionCommentAdapter
import com.hindu.joltt.Model.ConfessionCommentModel
import com.hindu.joltt.Model.ConfessionModel

class ConfessionCommentActivity : AppCompatActivity() {
    private var confessionId = ""
    private var confessorId = ""
    private var firebaseUser:FirebaseUser? = null
    private var commentList:MutableList<ConfessionCommentModel>? = null
    private var confessionCommentAdapter: ConfessionCommentAdapter? = null

    private lateinit var addcCommentButton:ImageView
    private lateinit var confessionTextComment:TextView
    private lateinit var addcCommentEditText:EditText
    private lateinit var Ccomment_empty_animation_c: LottieAnimationView
    private lateinit var nocCommentsText:TextView
    private lateinit var RecyclerViewComment_c:RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confession_comment)


        addcCommentButton = findViewById(R.id.addcCommentButton)
        confessionTextComment = findViewById(R.id.confessionTextComment)
        addcCommentEditText = findViewById(R.id.addcCommentEditText)
        Ccomment_empty_animation_c = findViewById(R.id.Ccomment_empty_animation_c)
        nocCommentsText = findViewById(R.id.nocCommentsText)
        RecyclerViewComment_c = findViewById(R.id.RecyclerViewComment_c)



        val intent = intent
        confessionId = intent.getStringExtra("confessionId").toString()
        confessorId = intent.getStringExtra("confessorId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        loadConfession()

        addcCommentButton.setOnClickListener { view->
            addComment(view)
        }

        val recyclerView:RecyclerView = findViewById(R.id.RecyclerViewComment_c)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        commentList = ArrayList()
        confessionCommentAdapter = ConfessionCommentAdapter(this,commentList as ArrayList<ConfessionCommentModel>,confessorId,confessionId)
        recyclerView.adapter = confessionCommentAdapter
        loadComments()
    }

    private fun loadConfession(){
        val data = FirebaseDatabase.getInstance().reference
            .child("Confession")
            .child(confessionId)
        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val value = snapshot.getValue(ConfessionModel::class.java)
                    confessionTextComment.text = value!!.confessionText
                }else{
                    confessionTextComment.text = "No internet connection"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                confessionTextComment.text = "No internet connection"
            }

        })
    }

    private fun addComment(view:View){
        if (addcCommentEditText.text.isEmpty()){
            Snackbar.make(view,"please write something..", Snackbar.LENGTH_SHORT).show()
        }else{
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("ConfessionComments")
                .child(confessionId)

            val commentId = dataRef.push().key

            val dataMap = HashMap<String,Any>()
            dataMap["cCommentId"] = commentId!!
            dataMap["cComment"] = addcCommentEditText.text.toString()
            dataMap["confessionId"] = confessionId
            dataMap["cPublisher"] = firebaseUser!!.uid

            dataRef.child(commentId).updateChildren(dataMap)
            addcCommentEditText.text.clear()
            addNotification()
        }
    }

    private fun loadComments(){
        val database = FirebaseDatabase.getInstance().reference
            .child("ConfessionComments")
            .child(confessionId)

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    commentList!!.clear()
                    nocCommentsText.visibility = View.GONE
                    Ccomment_empty_animation_c.visibility = View.GONE
                    RecyclerViewComment_c.visibility = View.VISIBLE

                    for (snapshot in snapshot.children){
                        val commment = snapshot.getValue(ConfessionCommentModel::class.java)
                        commentList!!.add(commment!!)
                    }
                    confessionCommentAdapter!!.notifyDataSetChanged()
                }else{
                    nocCommentsText.visibility = View.VISIBLE
                    Ccomment_empty_animation_c.visibility = View.VISIBLE
                    RecyclerViewComment_c.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                nocCommentsText.visibility = View.VISIBLE
                Ccomment_empty_animation_c.visibility = View.VISIBLE
                RecyclerViewComment_c.visibility = View.GONE
            }

        })
    }

    private fun addNotification(){
        if (confessorId != firebaseUser!!.uid){
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("Notification")
                .child("AllNotification")
                .child(confessorId)

            val notificationId = dataRef.push().key

            val dataMap = HashMap<String,Any>()
            dataMap["notificationId"] = notificationId!!
            dataMap["notificationText"] = "new comment on confession"+confessionTextComment.text.toString()
            dataMap["postID"] = confessionId
            dataMap["postN"] = false
            dataMap["pageN"] = false
            dataMap["confession"] = true
            dataMap["notifierId"] = firebaseUser!!.uid

            dataRef.push().setValue(dataMap)
            FirebaseDatabase.getInstance().reference
                .child("Notification")
                .child("UnReadNotification")
                .child(confessorId).child(notificationId).setValue(true)
        }
    }
}