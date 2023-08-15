package com.hindu.joltt.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.ChatActivity
import com.hindu.joltt.Activity.UserSupportActivity
import com.hindu.joltt.Adapter.MyPostAdapter
import com.hindu.joltt.Model.PostModel
import com.hindu.joltt.Model.RequestModel
import com.hindu.joltt.Model.UserModel
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.fragment_user_profiel.*
import kotlinx.android.synthetic.main.fragment_user_profiel.view.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfile : Fragment() {
    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser
    private var clicked= false

    private var recyclerView: RecyclerView? = null
    private var postAdapter: MyPostAdapter? = null
    private var mPost:MutableList<PostModel>? = null

    //Animations
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_user_profiel, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("uid","none")!!
        }

        //Social Link implementation
        root.github.setOnClickListener {
            val url = gitlink.text.toString()
            openInBrowser(url)
        }
        root.linkedin.setOnClickListener {
            val url = linkednLink.text.toString()
            openInBrowser(url)
        }
        root.portfolio.setOnClickListener {
            val url = portfolioLink.text.toString()
            openInBrowser(url)
        }
        root.instagram.setOnClickListener {
            val url = instagramLink.text.toString()
            openInBrowser(url)
        }
        root.threads.setOnClickListener {
            val url = threadsLink.text.toString()
            openInBrowser(url)
        }
        root.twitter.setOnClickListener {
            val url = twitterLink.text.toString()
            openInBrowser(url)
        }
        root.userMenu.setOnClickListener{
            addButtonClicked()
        }

        //About User FAB

        root.aboutUser.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_userDetails)
        }
        //Block User FAB
        root.blockUser.setOnClickListener {
            val intent = Intent(context, UserSupportActivity::class.java)
            intent.putExtra("uid",profileId)
            startActivity(intent)
        }

        //Chat User FAB
        root.chatUser.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid",profileId)
            startActivity(intent)
        }

        //Recycler View Declaration
        recyclerView = root.findViewById(R.id.usersPostRecyclerView)
        recyclerView!!.setHasFixedSize(true)
        val layoutManager: LinearLayoutManager = GridLayoutManager(context,2)
        recyclerView!!.layoutManager = layoutManager

        mPost = ArrayList()
        postAdapter = context?.let { MyPostAdapter(it,mPost as ArrayList<PostModel>) }
        recyclerView?.adapter = postAdapter
        postAdapter!!.notifyDataSetChanged()


        //Function Call

        CoroutineScope(Dispatchers.IO).launch {
            launch { privacy(root) }
            launch { userInfo() }
            launch { checkFollowAndFollowing(root) }
            launch { getFollowers(root) }
            launch { getFollowings(root) }
            launch { isBlocked(root)
                haveBlocked(root) }
            /*launch { retrievePost() }*/
        }

        //Follow button status
        root.infuseButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                checkPrivacy(root)
            }
        }

        //Total following and followers on click
        root.ll_followers_user.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.putString("title","Followers")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_showUserFragment)
        }

        root.ll_following_user.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.putString("title","Following")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_showUserFragment)
        }

        return root
    }

    //open in browser
    private fun openInBrowser(url:String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
    //Animating the FABs
    private fun addButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }
    private fun setVisibility(clicked:Boolean) {
        if (!clicked){
            blockUser.visibility = View.VISIBLE
            aboutUser.visibility = View.VISIBLE
            chatUser.visibility = View.VISIBLE
        }else{
            blockUser.visibility = View.GONE
            aboutUser.visibility = View.GONE
            chatUser.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked:Boolean) {
        if (!clicked){
            blockUser.startAnimation(fromBottom)
            aboutUser.startAnimation(fromBottom)
            chatUser.startAnimation(fromBottom)
            userMenu.startAnimation(rotateOpen)
        }else{
            blockUser.startAnimation(toBottom)
            blockUser.visibility = View.GONE
            aboutUser.startAnimation(toBottom)
            aboutUser.visibility = View.GONE
            chatUser.startAnimation(toBottom)
            chatUser.visibility = View.GONE
            userMenu.startAnimation(rotateClose)
            clearAnimation()
        }
    }

    private fun clearAnimation(){
        blockUser.clearAnimation()
        aboutUser.clearAnimation()
        chatUser.clearAnimation()
    }

    //User Info Retrieve Function
    private suspend fun userInfo(){

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userData = snapshot.getValue(UserModel::class.java)
                    Glide.with(context!!).load(userData!!.profileImage).into(userProfileImage)
                    userName_profile.text = userData.fullName
                    user_bio_profile.text = userData.bio
                    gitlink.text = userData.githubLink
                    linkednLink.text = userData.linkedin
                    portfolioLink.text = userData.portfolio
                    instagramLink.text = userData.instagram
                    twitterLink.text = userData.twitter
                    threadsLink.text = userData.threads


                    if (userData.githubLink.isNullOrEmpty()){
                        github.setImageResource(R.drawable.git_blur)
                        github.isClickable = false
                    }
                    if (userData.portfolio.isNullOrEmpty()){
                        portfolio.setImageResource(R.drawable.portfolio_blur)
                        portfolio.isClickable = false
                    }
                    if (userData.instagram.isNullOrEmpty()){
                        instagram.setImageResource(R.drawable.insta_blur)
                        instagram.isClickable = false
                    }
                    if (userData.twitter.isNullOrEmpty()){
                        twitter.setImageResource(R.drawable.twitter_blur)
                        twitter.isClickable = false
                    }
                    if (userData.linkedin.isNullOrEmpty()){
                        linkedin.setImageResource(R.drawable.linkedin_blur)
                        linkedin.isClickable = false
                    }
                    if (userData.threads.isNullOrEmpty()){
                        threads.setImageResource(R.drawable.threads_blur)
                        threads.isClickable = false
                    }

                    /*if (userData.verification){
                        verification_UserPage.visibility = View.VISIBLE
                    }*/
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("some error occurred")
            }
        })
    }
    //Privacy Status Function Function
    private suspend fun checkPrivacy(root:View){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.private){
                        when (root.infuseTxt.text.toString()){
                            "Infuse"->{
                                firebaseUser.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Users").child(profileId)
                                        .child("FollowRequest").child(it1.toString())
                                        .setValue(true)
                                    root.infuseTxt.text = "Requested"

                                    val ref = FirebaseDatabase.getInstance().reference
                                        .child("Users")
                                        .child(profileId)
                                        .child("Requesters")
                                    val requestMap = HashMap<String,Any>()
                                    requestMap["requesterId"] = FirebaseAuth.getInstance().currentUser!!.uid
                                    ref.child(firebaseUser.uid).updateChildren(requestMap)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        addNotification(message = "is requested to follow you")
                                    }
                                }
                            }
                            "Requested"->{
                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(profileId)
                                    .child("Requesters")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .removeValue()
                            }
                            "Unblock" ->{
                                FirebaseDatabase.getInstance().reference
                                    .child("Users")
                                    .child(firebaseUser.uid)
                                    .child("BlockedUsers")
                                    .child(profileId)
                                    .removeValue()
                            }
                            "Defuse" -> {
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
                            }
                        }

                    }else{

                        when (root.infuseTxt.text.toString()) {
                            "Infuse" -> {
                                firebaseUser.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(it1.toString())
                                        .child("Following").child(profileId)
                                        .setValue(true)
                                }

                                firebaseUser.uid.let {it1->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(profileId)
                                        .child("Followers").child(it1.toString())
                                        .setValue(true)
                                }
                                CoroutineScope(Dispatchers.IO).launch{
                                    addNotification(message = "is now following you")
                                }


                            }
                            "Defuse" -> {
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
                            }
                            "Unblock" ->{
                                FirebaseDatabase.getInstance().reference
                                    .child("Users")
                                    .child(firebaseUser.uid)
                                    .child("BlockedUsers")
                                    .child(profileId)
                                    .removeValue()
                                Toast.makeText(context,"User unblocked",Toast.LENGTH_SHORT).show()
                                root.infuseTxt.text="Infuse"
                            }
                            "Requested" ->{
                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(profileId)
                                    .child("Requesters")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .removeValue()
                            }
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Infuse Request Check
    private suspend fun checkRequested(root:View){
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(profileId)
            .child("Requesters")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataRef = snapshot.getValue(RequestModel::class.java)
                if (snapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()){
                    root.infuseTxt.text = "Requested"
                }else{
                    root.infuseTxt.text = "Infuse"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Check Following and Following Function
    private suspend fun checkFollowAndFollowing(root: View){
        val databaseRef =
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")

        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()){
                    root.infuseTxt.text= "Defuse"
                }else{
                    CoroutineScope(Dispatchers.IO).launch {
                        launch {checkRequested(root)  }
                        launch { haveBlocked(root) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Get Followers
    private suspend fun getFollowers(root: View){
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   root.totalFollowers.text = snapshot.childrenCount.toString()
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Get Following
    private suspend fun getFollowings(root: View){
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")
        followingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()-1
                    root.totalFollowing.text = count.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Get Privacy
    private suspend fun privacy(root: View){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.private) {
                        isCurrentUserInList { isInList->
                            if (isInList){
                                usersPostRecyclerView.visibility = View.VISIBLE
                                user_socialLinks_RL.visibility = View.VISIBLE
                                userMenu.visibility = View.VISIBLE
                                retrievePost()
                            }else{
                                privateAC_LL.visibility = View.VISIBLE
                                usersPostRecyclerView.visibility = View.GONE
                                user_socialLinks_RL.visibility = View.GONE
                                userMenu.visibility = View.GONE
                            }
                        }
                    }else{
                        retrievePost()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Is Blocked Function
    private suspend fun isBlocked(root: View){
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(profileId)
            .child("BlockedUsers")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataRef = snapshot.getValue(RequestModel::class.java)
                if (snapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()){
                    root.infuseButton.visibility = View.GONE
                    user_socialLinks_RL.visibility = View.GONE
                    userName_profile.text = "Username"
                    user_bio_profile.text = " "
                    rl_countDetails_user.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Check Block Status Function
    private suspend fun haveBlocked(root: View){
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid)
            .child("BlockedUsers")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataRef = snapshot.getValue(RequestModel::class.java)
                if (snapshot.child(profileId).exists()){
                    root.infuseTxt.text = "Unblock"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    //Add Notification
    private suspend fun addNotification(message:String){
        //sendNotification()
        if (profileId != FirebaseAuth.getInstance().currentUser!!.uid){
            val dataRef = FirebaseDatabase.getInstance()
                .reference.child("Notification")
                .child("AllNotification")
                .child(profileId)
            val notificationId = dataRef.push().key!!

            val dataMap = HashMap<String,Any>()
            dataMap["notificationId"] = notificationId
            dataMap["notificationText"] = message
            dataMap["postID"] = ""
            dataMap["postN"] = false
            dataMap["notifierId"] = FirebaseAuth.getInstance().currentUser!!.uid

            dataRef.push().setValue(dataMap)

            val databaseRef = FirebaseDatabase.getInstance().reference
                .child("Notification")
                .child("UnReadNotification")
                .child(profileId).child(notificationId).setValue(true)

        }
    }
    //User Post function
    private fun retrievePost(){
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")
        postRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mPost!!.clear()
                for(snapshot in snapshot.children){
                    val post = snapshot.getValue(PostModel::class.java)
                    if (post != null){
                        if(post.publisher == profileId){
                            mPost!!.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()

                    postcount_user.text = mPost?.size.toString()
                    if (mPost.isNullOrEmpty()){
                        noPostLayout_user.visibility = View.VISIBLE
                        othersPostLayout.visibility = View.GONE
                    }else{
                        noPostLayout_user.visibility = View.GONE
                        othersPostLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    //Checking current user is in users following list
    fun isCurrentUserInList(callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("Follow").child(profileId)
            .child("Following")

        //val query = usersRef.child(profileId).child("Following").orderByValue().equalTo(FirebaseAuth.getInstance().currentUser!!.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                    callback.invoke(true)
                }else{
                    callback.invoke(false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                println("Error: ${databaseError.message}")
                callback(false) // Assuming false when an error occurs
            }
        })
    }
}