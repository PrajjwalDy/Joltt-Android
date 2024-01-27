package com.hindu.joltt.Activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase.getInstance
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.CommentAdapter
import com.hindu.joltt.Model.CommentModel
import com.hindu.joltt.Model.Token
import com.hindu.joltt.Model.UserModel
import com.hindu.joltt.PushNotification.APIService
import com.hindu.joltt.PushNotification.Client
import com.hindu.joltt.PushNotification.Data
import com.hindu.joltt.PushNotification.MyResponse
import com.hindu.joltt.PushNotification.Sender
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentActivity : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""
    private var pageId = ""
    private var pageName = ""
    private var pageAdmin = ""
    private var page:Boolean = false
    private var firebaseUser: FirebaseUser? = null
    private var commentList:MutableList<CommentModel>? = null
    private var commentsAdapter: CommentAdapter? = null

    var notify = false
    var apiService: APIService? = null

    //objects
    private lateinit var back:ImageView
    private lateinit var addCommentButton:ImageView
    private lateinit var commentText:EditText

    private lateinit var emptyAnimation:LottieAnimationView
    private lateinit var emptyCommentText:TextView

    private lateinit var postPublisherName:TextView
    private lateinit var postCaption:TextView
    private lateinit var postPublisherProfile:CircleImageView

    private lateinit var commentPublisherProfile:CircleImageView
    private lateinit var commentRecyclerView:RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)

        //OBJECT DECLARATION

        back = findViewById(R.id.commentBack)
        addCommentButton = findViewById(R.id.addCommentButton)
        commentText = findViewById(R.id.addCommentEditText)
        emptyAnimation = findViewById(R.id.comment_empty_animation)
        emptyCommentText = findViewById(R.id.noCommentsText)
        postPublisherName = findViewById(R.id.publisherNameComment)
        postCaption = findViewById(R.id.postCaptionComment)
        postPublisherProfile = findViewById(R.id.publisherProfileComment)
        commentPublisherProfile = findViewById(R.id.currentUserProfileComment)
        commentRecyclerView = findViewById(R.id.RecyclerViewComment)




        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisher").toString()
        pageAdmin = intent.getStringExtra("pageAdmin").toString()
        pageName = intent.getStringExtra("pageName").toString()
        page = intent.getBooleanExtra("page",false)
        pageId = intent.getStringExtra("pageId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView: RecyclerView = findViewById(R.id.RecyclerViewComment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        commentList = ArrayList()
        commentsAdapter = CommentAdapter(this, commentList as ArrayList<CommentModel>,publisherId,postId)
        recyclerView.adapter = commentsAdapter

        displayCaption()
        loadComments()
        userInfo()
        updateToken()


        addCommentButton.setOnClickListener { view->
            addComment(view)
        }
        back.setOnClickListener {
            finish()
        }

    }

    private fun addComment(view:View){
        if (commentText.text.isEmpty()){
            Snackbar.make(view,"please write something...", Snackbar.LENGTH_SHORT).show()
        }else{
            val dataRef = getInstance().reference
                .child("Comments")
                .child(postId)

            val commentId = dataRef.push().key

            val dataMap = HashMap<String,Any>()
            dataMap["commentId"] = commentId!!
            dataMap["commentText"] = commentText.text.toString()
            dataMap["commenter"] = firebaseUser!!.uid

            dataRef.child(commentId).updateChildren(dataMap)
            commentText.text.clear()
            if (page){
                addPageNotification()
            }else{
                addNotification()
                sendNotification()
            }
        }
    }

    private fun userInfo(){
        val usersRef = getInstance().reference
            .child("Users")
            .child(firebaseUser!!.uid)

        usersRef.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        val user = snapshot.getValue(UserModel::class.java)
                        Glide.with(this@CommentActivity).load(user!!.profileImage).into(commentPublisherProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    private fun loadComments(){
        val databaseRef = getInstance().reference
            .child("Comments")
            .child(postId)
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    commentList!!.clear()
                    emptyCommentText.visibility = View.GONE
                    emptyAnimation.visibility = View.GONE
                    commentRecyclerView.visibility = View.VISIBLE
                    for (snapshot in snapshot.children){
                        val comment = snapshot.getValue(CommentModel::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentsAdapter!!.notifyDataSetChanged()

                }else{
                    emptyCommentText.visibility = View.VISIBLE
                    commentRecyclerView.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                emptyCommentText.visibility = View.VISIBLE
                commentRecyclerView.visibility = View.GONE
            }

        })
    }

    private fun displayCaption(){
        publisherInfo()
        val postRef = getInstance().reference.child("Post")
            .child(postId).child("caption")

        postRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val caption = snapshot.value.toString()
                    postCaption.text = caption
                }else if (postCaption.text ==null){
                    postCaption.text = "No Caption Added"
                    postCaption.maxLines = 2
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun publisherInfo(){
        val userDataRef = getInstance().reference
            .child("Users")
            .child(publisherId)
        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(this@CommentActivity).load(data!!.profileImage).into(postPublisherProfile)
                    postPublisherName.text = data.fullName

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addNotification(){
        //sendNotification()
        if (publisherId != FirebaseAuth.getInstance().currentUser!!.uid){
            val dataRef = getInstance()
                .reference.child("Notification")
                .child("AllNotification")
                .child(publisherId)
            val notificationId = dataRef.push().key!!

            val dataMap = HashMap<String,Any>()
            dataMap["notificationId"] = notificationId
            dataMap["notificationText"] = "Commented on your post"+commentText.text.toString()
            dataMap["postID"] = postId
            dataMap["postN"] = true
            dataMap["notifierId"] = FirebaseAuth.getInstance().currentUser!!.uid

            dataRef.push().setValue(dataMap)

            val databaseRef = getInstance().reference
                .child("Notification")
                .child("UnReadNotification")
                .child(publisherId).child(notificationId).setValue(true)

        }
    }

    private fun addPageNotification(){
        if (publisherId != FirebaseAuth.getInstance().currentUser!!.uid){
            val dataRef = getInstance()
                .reference.child("Notification")
                .child("AllNotifications")
                .child(pageAdmin)
            val notificationId = dataRef.push().key!!

            val dataMap = HashMap<String,Any>()
            dataMap["notificationId"] = notificationId
            dataMap["notificationText"] = "you have new notifications on page:"+pageName
            dataMap["postID"] = postId
            dataMap["postN"] = true
            dataMap["pageN"] = true
            dataMap["notifierId"] = FirebaseAuth.getInstance().currentUser!!.uid

            dataRef.push().setValue(dataMap)

            val dataNRef = getInstance()
                .reference.child("PageNotification")
                .child("AllNotification")
                .child(pageAdmin)
                .child(pageId)

            val nId = dataNRef.push().key!!

            val dataNMap = HashMap<String,Any>()
            dataNMap["nId"] = nId
            dataNMap["nText"] = "You have new notifications for page:"+pageName
            dataNMap["postImage"] = postId
            dataNMap["iPost"] = true
            dataNMap["pageId"] = pageId
            dataNMap["notifier"] = FirebaseAuth.getInstance().currentUser!!.uid

            dataNRef.child(nId).updateChildren(dataNMap)

            getInstance().reference
                .child("Notification")
                .child("UnReadNotification")
                .child(pageAdmin).child(notificationId).setValue(true)

        }
    }

    private fun sendNotification(){
        val notificationRef = getInstance().reference.child("Tokens").child(publisherId)

        notificationRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val token = snapshot.getValue(Token::class.java)?.token
                    if (token != null){
                        val data2 = Data(firebaseUser!!.uid,
                            R.mipmap.ic_launcher,
                            "Commented on your post"+commentText.text.toString(),
                            "Post",
                            publisherId)


                        val sender = Sender(data2,token.toString())

                        apiService!!.sendNotification(sender)
                            .enqueue(object : Callback<MyResponse?>{
                                override fun onResponse(
                                    call: Call<MyResponse?>,
                                    response: Response<MyResponse?>
                                ) {
                                    if (response.code() == 200)
                                        if (response.body()!!.success !==1){

                                            Toast.makeText(this@CommentActivity,"Failed,Nothing Happened $token",Toast.LENGTH_SHORT).show()
                                        }
                                }

                                override fun onFailure(call: Call<MyResponse?>, t: Throwable) {
                                    TODO("Not yet implemented")
                                }

                            })

                    }else{
                        Toast.makeText(this@CommentActivity,"No,Token Found $publisherId",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun updateToken(){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {task->
            if (!task.isSuccessful){
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            val newToken = Token(token)
            getInstance().reference.child("Tokens")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(newToken)
        })

    }
}