package com.hindu.joltt.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Activity.EditProfileActivity
import com.hindu.joltt.Activity.SettingActivity
import com.hindu.joltt.Adapter.MyPostAdapter
import com.hindu.joltt.Model.PostModel
import com.hindu.joltt.Model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var postAdapter: MyPostAdapter? = null
    private var mPost:MutableList<PostModel>? = null
    private var clicked= false

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId:String

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }
    private val toInvisibility: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_invisibility) }
    private val toVisibility: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_visibility) }

    private lateinit var github_profile:ImageView
    private lateinit var linkedin_profile:ImageView
    private lateinit var portfolio_profile:ImageView
    private lateinit var instagram_profile:ImageView
    private lateinit var threads_profile:ImageView
    private lateinit var twitter_profile:ImageView
    private lateinit var ll_followers:LinearLayout
    private lateinit var ll_following:LinearLayout
    private lateinit var profileMenu:FloatingActionButton
    private lateinit var settingFab:FloatingActionButton
    private lateinit var editProfile_fab:FloatingActionButton

    private lateinit var profileImage:ImageView

    private lateinit var userBio:TextView
    private lateinit var userFullName:TextView
    private lateinit var gitlink_profile:TextView
    private lateinit var linkednLink_profile:TextView
    private lateinit var instagramLink_profile:TextView
    private lateinit var twitterLink_profile:TextView
    private lateinit var threadsLink_profile:TextView
    private lateinit var portfolioLink_profile:TextView

    private lateinit var totalFollowers_user:TextView
    private lateinit var totalFollowing_user:TextView

    private lateinit var postCount_profile:TextView

    private lateinit var noPostLayout:LinearLayout
    private lateinit var myPostLayout:RelativeLayout




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.profile_fragment, container, false)


        github_profile = root.findViewById(R.id.github_profile)
        linkedin_profile = root.findViewById(R.id.linkedin_profile)
        portfolio_profile = root.findViewById(R.id.portfolio_profile)
        instagram_profile = root.findViewById(R.id.instagram_profile)
        threads_profile = root.findViewById(R.id.threads_profile)
        twitter_profile = root.findViewById(R.id.twitter_profile)
        ll_followers = root.findViewById(R.id.ll_followers)
        ll_following = root.findViewById(R.id.ll_following)
        profileMenu = root.findViewById(R.id.profileMenu)
        settingFab = root.findViewById(R.id.settingFab)
        editProfile_fab = root.findViewById(R.id.editProfile_fab)
        profileImage = root.findViewById(R.id.profileImage)
        userBio = root.findViewById(R.id.userBio)

        userFullName = root.findViewById(R.id.userFullName)
        gitlink_profile = root.findViewById(R.id.gitlink_profile)
        linkednLink_profile = root.findViewById(R.id.linkednLink_profile)
        instagramLink_profile = root.findViewById(R.id.instagramLink_profile)
        twitterLink_profile = root.findViewById(R.id.twitterLink_profile)
        threadsLink_profile = root.findViewById(R.id.threadsLink_profile)
        portfolioLink_profile = root.findViewById(R.id.portfolioLink_profile)
        totalFollowers_user = root.findViewById(R.id.totalFollowers_user)
        totalFollowing_user = root.findViewById(R.id.totalFollowing_user)
        postCount_profile = root.findViewById(R.id.postCount_profile)
        noPostLayout = root.findViewById(R.id.noPostLayout)
        myPostLayout = root.findViewById(R.id.myPostLayout)



        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("uid","none")!!
        }

        CoroutineScope(Dispatchers.IO).launch {
            launch { userInfo() }
            launch { getFollowers(root) }
            launch {
                getFollowings(root)
            }
        }

        github_profile.setOnClickListener {
            val url = gitlink_profile.text.toString()
            openInBrowser(url)
        }
        linkedin_profile.setOnClickListener {
            val url = linkednLink_profile.text.toString()
            openInBrowser(url)
        }
        portfolio_profile.setOnClickListener {
            val url = portfolioLink_profile.text.toString()
            openInBrowser(url)
        }
        instagram_profile.setOnClickListener {
            val url = instagramLink_profile.text.toString()
            openInBrowser(url)
        }
        threads_profile.setOnClickListener {
            val url = threadsLink_profile.text.toString()
            openInBrowser(url)
        }
        twitter_profile.setOnClickListener {
            val url = twitterLink_profile.text.toString()
            openInBrowser(url)
        }



        ll_followers.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            pref.putString("title","Followers")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_navigation_profile_to_showUserFragment)
        }

        ll_following.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref!!.putString("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            pref.putString("title","Following")
            pref.apply()
            Navigation.findNavController(root).navigate(R.id.action_navigation_profile_to_showUserFragment)
        }

        recyclerView = root.findViewById(R.id.myPostRV)
        recyclerView!!.setHasFixedSize(true)
        val layoutManager:LinearLayoutManager = GridLayoutManager(context,2)
        recyclerView!!.layoutManager = layoutManager

        mPost = ArrayList()
        postAdapter = context?.let { MyPostAdapter(it,mPost as ArrayList<PostModel>,"MyProfile") }
        recyclerView?.adapter = postAdapter
        postAdapter!!.notifyDataSetChanged()

        CoroutineScope(Dispatchers.IO).launch {
            launch { retrievePost() }
        }

        profileMenu.setOnClickListener {
            addButtonClicked()
        }

        settingFab.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
        }

        editProfile_fab.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        profileImage.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_profile_to_myDetailsFragment)
        }

        return  root
    }

    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userData = snapshot.getValue(UserModel::class.java)
                    context?.let { Glide.with(it).load(userData!!.profileImage).into(profileImage) }
                    userFullName.text = userData!!.fullName
                    userBio.text = userData.bio
                    gitlink_profile.text = userData.githubLink
                    linkednLink_profile.text = userData.linkedin
                    portfolioLink_profile.text = userData.portfolio
                    instagramLink_profile.text = userData.instagram
                    twitterLink_profile.text = userData.twitter
                    threadsLink_profile.text = userData.threads


                    if (userData.githubLink.isNullOrEmpty()){
                        github_profile.setImageResource(R.drawable.git_blur)
                        github_profile.isClickable = false
                    }
                    if (userData.portfolio.isNullOrEmpty()){
                        portfolio_profile.setImageResource(R.drawable.portfolio_blur)
                        portfolio_profile.isClickable = false
                    }
                    if (userData.instagram.isNullOrEmpty()){
                        instagram_profile.setImageResource(R.drawable.insta_blur)
                        instagram_profile.isClickable = false
                    }
                    if (userData.twitter.isNullOrEmpty()){
                        twitter_profile.setImageResource(R.drawable.twitter_blur)
                        twitter_profile.isClickable = false
                    }
                    if (userData.linkedin.isNullOrEmpty()){
                        linkedin_profile.setImageResource(R.drawable.linkedin_blur)
                        linkedin_profile.isClickable = false
                    }
                    if (userData.threads.isNullOrEmpty()){
                        threads_profile.setImageResource(R.drawable.threads_blur)
                        threads_profile.isClickable = false
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("some error occurred")
            }
        })
    }
    private fun getFollowers(root: View){
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Followers")

        followingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()
                    totalFollowers_user.text = count.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getFollowings(root: View){
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")
        followingRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()-1
                    totalFollowing_user.text = count.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun retrievePost(){
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")
        postRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mPost!!.clear()
                for(snapshot in snapshot.children){
                    val post = snapshot.getValue(PostModel::class.java)
                    if (post != null){
                        if(post.publisher == FirebaseAuth.getInstance().currentUser!!.uid){
                            mPost!!.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                    postCount_profile.text = mPost?.size.toString()
                    if (mPost.isNullOrEmpty()){
                        noPostLayout?.visibility = View.VISIBLE
                        myPostLayout?.visibility = View.GONE
                    }else{
                        noPostLayout?.visibility = View.GONE
                        myPostLayout?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun addButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }
    private fun setVisibility(clicked:Boolean) {
        if (!clicked){
            editProfile_fab.visibility = View.VISIBLE
            settingFab.visibility = View.VISIBLE
        }else {
            editProfile_fab.visibility = View.GONE
            settingFab.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked:Boolean) {
        if (!clicked){
            editProfile_fab.startAnimation(fromBottom)
            settingFab.startAnimation(fromBottom)
            profileMenu.startAnimation(rotateOpen)
            profileMenu.setImageResource(R.drawable.clos_angle)
        }else{
            editProfile_fab.startAnimation(toBottom)
            settingFab.startAnimation(toBottom)
            profileMenu.startAnimation(rotateClose)
            profileMenu.setImageResource(R.drawable.menu)
        }
    }


    private fun openInBrowser(url:String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    //User Post init function

}