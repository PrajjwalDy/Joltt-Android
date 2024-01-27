package com.hindu.joltt.Activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.CommunityRepliesAdapter
import com.hindu.joltt.Model.CommunityReplyModel
import com.hindu.joltt.Model.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityRepliesActivity : AppCompatActivity() {
    private var id =""
    private var firebaseUser: FirebaseUser? = null
    private var communityRepliesList:MutableList<CommunityReplyModel>? = null
    private var communityRepliesAdapterAdapter: CommunityRepliesAdapter? = null



    //OBJECTS

    private lateinit var reply_empty_animation:LottieAnimationView
    private lateinit var addReplyButton:ImageView
    private lateinit var addReplyEditText:EditText
    private lateinit var noReplyText:TextView
    private lateinit var currentUserProfileReply:CircleImageView
    private lateinit var replies_RV:RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_replies)


        //OBJECTS DECLARATIONS
        replies_RV = findViewById(R.id.replies_RV)
        currentUserProfileReply = findViewById(R.id.currentUserProfileReply)
        noReplyText = findViewById(R.id.noReplyText)
        addReplyEditText = findViewById(R.id.addReplyEditText)
        reply_empty_animation = findViewById(R.id.reply_empty_animation)
        addReplyButton = findViewById(R.id.addReplyButton)


        val intent = intent
        id = intent.getStringExtra("communityId").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser


        val recyclerView: RecyclerView = findViewById(R.id.replies_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        communityRepliesList = ArrayList()
        communityRepliesAdapterAdapter = CommunityRepliesAdapter(this, communityRepliesList as ArrayList<CommunityReplyModel>,id)
        recyclerView.adapter = communityRepliesAdapterAdapter

        addReplyButton.setOnClickListener { view->
            addComment(view)
        }
        CoroutineScope(Dispatchers.IO).launch {
            launch { userInfo() }
            launch { loadComments() }
        }
    }

    private fun addComment(view: View){
        if (addReplyEditText.text.isEmpty()){
            Snackbar.make(view,"please write something..", Snackbar.LENGTH_SHORT).show()
        }else{
            val dataRef = FirebaseDatabase.getInstance().reference
                .child("Community")
                .child(id)
                .child("replies")

            val commentId = dataRef.push().key

            val dataMap = HashMap<String,Any>()
            dataMap["replyId"] = commentId!!
            dataMap["replyText"] = addReplyEditText.text.toString()
            dataMap["replierId"] = firebaseUser!!.uid
            dataMap["communityId"] = id

            dataRef.child(commentId).updateChildren(dataMap)
            addReplyEditText.text.clear()

        }
    }

    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(firebaseUser!!.uid)

        usersRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        val user = snapshot.getValue(UserModel::class.java)
                        Glide.with(this@CommunityRepliesActivity).load(user!!.profileImage).into(currentUserProfileReply)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            }
        )
    }

    private fun loadComments(){
        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("Community")
            .child(id)
            .child("replies")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    communityRepliesList!!.clear()
                    noReplyText.visibility = View.GONE
                    reply_empty_animation.visibility = View.GONE
                    replies_RV.visibility = View.VISIBLE
                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(CommunityReplyModel::class.java)
                        communityRepliesList!!.add(comment!!)
                    }
                    communityRepliesAdapterAdapter!!.notifyDataSetChanged()

                }else{
                    noReplyText.visibility = View.VISIBLE
                    replies_RV.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                noReplyText.visibility = View.VISIBLE
                replies_RV.visibility = View.GONE
            }

        })
    }
}