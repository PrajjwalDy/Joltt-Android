package com.hindu.cunow.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.ChatActivity
import com.hindu.cunow.Activity.ShowUsersActivity
import com.hindu.cunow.Activity.UserSupportActivity
import com.hindu.cunow.Model.RequestModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.fragment_user_profiel.*
import kotlinx.android.synthetic.main.fragment_user_profiel.view.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.verification_profilePage
import kotlinx.android.synthetic.main.profile_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserProfile : Fragment() {
    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser

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

        root.open_options_user.setOnClickListener {
            root.userProfile_LL.visibility = View.VISIBLE
            root.open_options_user.visibility = View.GONE
            root.close_options_user.visibility = View.VISIBLE
        }

        root.close_options_user.setOnClickListener {
            root.userProfile_LL.visibility = View.GONE
            root.open_options_user.visibility = View.VISIBLE
            root.close_options_user.visibility = View.GONE
        }

        root.photosUsers.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_userPostsFragment)
        }

        root.aboutUser.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_userDetails)
        }

        root.findSupportUser.setOnClickListener {
            val intent = Intent(context,UserSupportActivity::class.java)
            intent.putExtra("uid",profileId)
            startActivity(intent)
        }

        root.chat_ll.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("uid",profileId)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            launch { privacy(root) }
            launch { userInfo() }
            launch { checkFollowAndFollowing(root) }
            launch { getFollowers(root) }
            launch { getFollowings(root) }
            launch { isBlocked(root)
                haveBlocked(root) }
        }


        root.follow_unfollow_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                checkPrivacy(root)
            }
        }

        root.totalFollowers_user.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.putString("title","Followers")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_showUserFragment)
        }

        root.totalFollowing_user.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",profileId)
            pref.putString("title","Following")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_userProfile_to_showUserFragment)
        }

        return root
    }

    private suspend fun userInfo(){

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userData = snapshot.getValue(UserModel::class.java)
                    Glide.with(context!!).load(userData!!.profileImage).into(userProfileImage)
                    userName_profile.text = userData.fullName
                    user_bio_profile.text = userData.bio
                    if (userData.verification){
                        verification_UserPage.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("some error occurred")
            }
        })
    }

    private suspend fun checkPrivacy(root:View){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.private){
                        when (root.follow_unfollow_button.text.toString()){
                            "Follow"->{
                                firebaseUser.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Users").child(profileId)
                                        .child("FollowRequest").child(it1.toString())
                                        .setValue(true)
                                    root.follow_unfollow_button.text = "Requested"

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
                            "Following" -> {
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

                        when (root.follow_unfollow_button.text.toString()) {
                            "Follow" -> {
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
                            "Following" -> {
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

    private suspend fun checkRequested(root:View){
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(profileId)
            .child("Requesters")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataRef = snapshot.getValue(RequestModel::class.java)
                if (snapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()){
                    root.follow_unfollow_button.text = "Requested"
                }else{
                    root.follow_unfollow_button.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun checkFollowAndFollowing(root: View){
        val databaseRef =
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")

        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()){
                    root.follow_unfollow_button.text= "Following"
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

    private suspend fun getFollowers(root: View){
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   root.totalFollowers_user.text = snapshot.childrenCount.toString()
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun getFollowings(root: View){
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")
        followingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()-1
                    root.totalFollowing_user.text = count.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun privacy(root: View){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.private) {
                        ll_moreOption_user.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun isBlocked(root: View){
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(profileId)
            .child("BlockedUsers")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataRef = snapshot.getValue(RequestModel::class.java)
                if (snapshot.child(FirebaseAuth.getInstance().currentUser!!.uid).exists()){
                    root.follow_unfollow_button.visibility = View.GONE
                    ll_moreOption_user.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun haveBlocked(root: View){
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid)
            .child("BlockedUsers")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataRef = snapshot.getValue(RequestModel::class.java)
                if (snapshot.child(profileId).exists()){
                    root.follow_unfollow_button.text = "Unblock"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

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

}