package com.hindu.cunow.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.AddPostActivity
import com.hindu.cunow.Activity.AddVibesAcitvity
import com.hindu.cunow.Activity.VideoUploadActivity
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Fragments.Circle.CircleTabActivity
import com.hindu.cunow.Model.DevMessageModel
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.image_or_video_dialogbox.view.*
import kotlinx.android.synthetic.main.more_option_dialogbox.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var checker = ""
    var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var clicked= false

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }
    private val toInvisibility: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_invisibility) }
    private val toVisibility: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_visibility) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        homeViewModel.postModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            postAdapter = context?.let { it1-> PostAdapter(it1, it as List<PostModel>) }
            recyclerView!!.adapter = postAdapter
            postAdapter!!.notifyDataSetChanged()

        })


        CoroutineScope(Dispatchers.IO).launch(){
            launch { checkFirstVisit() }
            launch {developerMessage()}
            launch {chatNotification()}
        }

        root.create.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.image_or_video_dialogbox, null)

            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()
            dialogView.selectImage.setOnClickListener {
                startActivity(Intent(context,AddPostActivity::class.java))
                alertDialog.dismiss()
            }
            dialogView.selectVideo.setOnClickListener {
                startActivity(Intent(context,VideoUploadActivity::class.java))
                alertDialog.dismiss()
            }

        }

        root.imin.setOnClickListener {
            updateVisit(root)
        }

        root.people_welcome.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_peopleFragment)
        }

        root.confession_welcome.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_confessionRoomFragment)
        }

        root.circle_welcome.setOnClickListener {
            val intent = Intent(context, CircleTabActivity::class.java)
            startActivity(intent)
        }

        root.closeMessage_btn.setOnClickListener {
            root.developerMessage_CV.visibility = View.GONE
        }

        root.chat.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_chatFragment)
        }

        root.create_post_fab.setOnClickListener {
            addButtonClicked()
        }

        root.add_image.setOnClickListener {
            startActivity(Intent(context,AddPostActivity::class.java))
        }

        root.add_video.setOnClickListener {
            startActivity(Intent(context,VideoUploadActivity::class.java))
        }

        root.add_text.setOnClickListener {

        }

        return root
    }

    private fun updateVisit(view: View){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users")
        val postMap = HashMap<String,Any>()
        postMap["firstVisit"] = false
        ref.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(postMap)
        CoroutineScope(Dispatchers.IO).launch {
            checkFirstVisit()
        }
    }
    private fun initView(root:View){
        recyclerView = root.findViewById(R.id.postRecyclerView) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        //loadUserImage(root)

        recyclerView!!.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy >0 && create_post_fab.visibility == View.VISIBLE){
                    //create_post_fab.startAnimation(toInvisibility)
                    root.create_post_fab.visibility = View.GONE
                }else if(dy<0 && create_post_fab.visibility == View.GONE){
                    //create_post_fab.startAnimation(toVisibility)
                    root.create_post_fab.visibility = View.VISIBLE
                }
            }
        })

    }
    private suspend fun checkFirstVisit(){
        val dataRef = FirebaseDatabase
            .getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        dataRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!!.firstVisit){
                        welcome_screen.visibility = View.VISIBLE
                        //postLayout_ll.visibility = View.GONE
                    }else{
                        postLayout_ll.visibility = View.VISIBLE
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    checkFollowingList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private suspend fun checkFollowingList(){
        checkPost()
        val database = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val count = snapshot.childrenCount.toInt()
                    if (count <=1 && checker == "no" ){
                        ll_empty_posts.visibility = View.VISIBLE
                        postRecyclerView.visibility = View.GONE
                    }else{
                        postRecyclerView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private  fun checkPost(){
        val database = FirebaseDatabase.getInstance().reference
            .child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("MyPosts")

            database.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        checker = "yes"
                    }else{
                        checker = "no"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private suspend fun developerMessage(){
        val database = FirebaseDatabase.getInstance().reference.child("DevMessage")
        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    developerMessage_CV.visibility = View.VISIBLE
                    val data = snapshot.getValue(DevMessageModel::class.java)
                    dev_message_tv.text = data!!.message
                }else{
                    developerMessage_CV.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private suspend fun chatNotification(){
        val data = FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        data.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    chatNotification_Count.visibility = View.VISIBLE
                    chatNotification_Count_text.text = snapshot.childrenCount.toString()
                }else{
                    chatNotification_Count.visibility = View.GONE
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
            add_image.visibility = View.VISIBLE
            add_text.visibility = View.VISIBLE
            add_video.visibility = View.VISIBLE
        }else{
            add_image.visibility = View.GONE
            add_text.visibility = View.GONE
            add_video.visibility = View.GONE
        }
    }
    private fun setAnimation(clicked:Boolean) {
        if (!clicked){
            add_text.startAnimation(fromBottom)
            add_image.startAnimation(fromBottom)
            add_video.startAnimation(fromBottom)
            create_post_fab.startAnimation(rotateOpen)
        }else{
            add_text.startAnimation(toBottom)
            add_image.startAnimation(toBottom)
            add_video.startAnimation(toBottom)
            create_post_fab.startAnimation(rotateClose)
        }
    }
}