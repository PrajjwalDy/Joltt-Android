package com.hindu.cunow.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Activity.AddPostActivity
import com.hindu.cunow.Activity.VideoUploadActivity
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Fragments.Chat.ChatFragment
import com.hindu.cunow.Model.DevMessageModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.addText_ET
import kotlinx.android.synthetic.main.fragment_home.add_image
import kotlinx.android.synthetic.main.fragment_home.add_text
import kotlinx.android.synthetic.main.fragment_home.add_video
import kotlinx.android.synthetic.main.fragment_home.chatNotification_Count
import kotlinx.android.synthetic.main.fragment_home.create_post_fab
import kotlinx.android.synthetic.main.fragment_home.dev_message_tv
import kotlinx.android.synthetic.main.fragment_home.developerMessage_CV
import kotlinx.android.synthetic.main.fragment_home.ll_chatcount
import kotlinx.android.synthetic.main.fragment_home.ll_empty_posts
import kotlinx.android.synthetic.main.fragment_home.postLayout_ll
import kotlinx.android.synthetic.main.fragment_home.postRecyclerView
import kotlinx.android.synthetic.main.fragment_home.view.add_image
import kotlinx.android.synthetic.main.fragment_home.view.add_text
import kotlinx.android.synthetic.main.fragment_home.view.add_video
import kotlinx.android.synthetic.main.fragment_home.view.closeMessage_btn
import kotlinx.android.synthetic.main.fragment_home.view.closeOnlyText
import kotlinx.android.synthetic.main.fragment_home.view.create_post_fab
import kotlinx.android.synthetic.main.fragment_home.view.developerMessage_CV
import kotlinx.android.synthetic.main.fragment_home.view.imin
import kotlinx.android.synthetic.main.fragment_home.view.onlyText_CV
import kotlinx.android.synthetic.main.fragment_home.view.strike
import kotlinx.android.synthetic.main.fragment_home.view.uploadTextBtn
import kotlinx.android.synthetic.main.fragment_home.welcome_screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_user_details.proceed_btn_0

class HomeFragment : Fragment() {
    private var checker = ""
    var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var clicked = false
    // private var postList: MutableList<PostModel>? = null

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.to_bottom_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        checkFirstVisit()

        homeViewModel.postModel!!.observe(viewLifecycleOwner, Observer {
            initView(root)
            postAdapter = context?.let { it1 -> PostAdapter(it1, it) }
            recyclerView!!.adapter = postAdapter
            postAdapter!!.notifyDataSetChanged()

        })


        CoroutineScope(Dispatchers.IO).launch() {
            launch { developerMessage() }
            launch { chatNotification() }
        }

        //first time visit
        root.imin.setOnClickListener {
            updateVisit(root)
        }

        //Close Developer Message
        root.closeMessage_btn.setOnClickListener {
            root.developerMessage_CV.visibility = View.GONE
        }

        //Create Post Floating Action Button
        root.create_post_fab.setOnClickListener {
            addButtonClicked()
        }

        root.add_image.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java))
            addButtonClicked()
        }

        root.add_video.setOnClickListener {
            startActivity(Intent(context, VideoUploadActivity::class.java))
            addButtonClicked()
        }

        root.add_text.setOnClickListener {
            root.onlyText_CV.visibility = View.VISIBLE
            addButtonClicked()
        }

        root.uploadTextBtn.setOnClickListener {
            if (addText_ET.text.isEmpty()) {
                Toast.makeText(context, "Please Write something", Toast.LENGTH_SHORT).show()
            } else {
                val dataRef = FirebaseDatabase.getInstance().reference.child("Post")
                val postId = dataRef.push().key
                val dataMap = HashMap<String, Any>()

                dataMap["postId"] = postId!!
                dataMap["caption"] = addText_ET.text.toString()
                dataMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                dataMap["iImage"] = false
                dataMap["video"] = false
                dataMap["page"] = false
                dataMap["public"] = false
                dataRef.child(postId).updateChildren(dataMap)
                buildHasTag(postId)
                Toast.makeText(context, "Post add successfully", Toast.LENGTH_SHORT).show()
                root.onlyText_CV.visibility = View.GONE
                addText_ET.text.clear()
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                // Hide the soft input method
                inputMethodManager.hideSoftInputFromWindow(root.windowToken, 0)
            }

        }

        root.closeOnlyText.setOnClickListener {
            root.onlyText_CV.visibility = View.GONE
        }

        root.strike.setOnClickListener {

            Navigation.findNavController(root)
                .navigate(R.id.action_navigation_home_to_chatFragment)
        }
        return root
    }

    private fun updateVisit(view: View) {
        FirebaseDatabase.getInstance().reference
            .child("FirstVisit")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .removeValue()
    }

    //BUILD HASHTAG
    private fun buildHasTag(postId: String) {
        val sentence = addText_ET.text.toString().trim { it <= ' ' }
        val words = sentence.split(" ")

        // Initialize an empty list of hashtags
        val hashtags = mutableListOf<String>()

        // Extract hashtags from the words
        for (word in words) {
            if (word.startsWith("#")) {
                hashtags.add(word)
            }
        }
        val hashtagsRef = FirebaseDatabase.getInstance().getReference("hashtags")

        for (hashtag in hashtags) {
            val key = hashtag.toString().removeRange(0, 1)
            val tagMap = HashMap<String, Any>()
            tagMap["tagName"] = hashtag
            hashtagsRef.child(key).updateChildren(tagMap)
            hashtagsRef.child(key).child("posts").child(postId).setValue(true)
            getPostCount(hashtag)
        }
    }

    //POST COUNT
    private fun getPostCount(tag: String) {
        val key = tag.removePrefix("#")
        val dataRef = FirebaseDatabase.getInstance()
            .reference.child("hashtags")
            .child(key)
            .child("posts")

        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hashtagsRef = FirebaseDatabase.getInstance().reference
                        .child("hashtags")
                        .child(key)
                    val tagMap = HashMap<String, Any>()
                    tagMap["postCount"] = snapshot.childrenCount.toInt()
                    hashtagsRef.updateChildren(tagMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.postRecyclerView) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        //loadUserImage(root)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    create_post_fab.show()
                } else {
                    create_post_fab.hide()
                }
            }
        })

    }

    private fun checkFirstVisit() {
        val dataRef = FirebaseDatabase
            .getInstance().reference.child("FirstVisit")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    welcome_screen.visibility = View.VISIBLE
                }else{
                    welcome_screen.visibility = View.GONE
                    postLayout_ll.visibility = View.VISIBLE
                }
                /*CoroutineScope(Dispatchers.IO).launch {
                    checkFollowingList()
                }*/
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    /*private suspend fun checkFollowingList() {
        checkPost()
        val database = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val count = snapshot.childrenCount.toInt()
                    if (count <= 1 && checker == "no") {
                        ll_empty_posts?.visibility = View.VISIBLE
                        postRecyclerView?.visibility = View.GONE
                    } else {
                        postRecyclerView?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun checkPost() {
        val database = FirebaseDatabase.getInstance().reference
            .child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("MyPosts") || snapshot.hasChild("FollowingPages")) {
                    checker = "yes"
                } else {
                    checker = "no"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun developerMessage() {
        val database = FirebaseDatabase.getInstance().reference.child("DevMessage")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    developerMessage_CV.visibility = View.VISIBLE
                    val data = snapshot.getValue(DevMessageModel::class.java)
                    dev_message_tv.text = data!!.message
                } else {
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

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    ll_chatcount?.visibility =View.VISIBLE
                    chatNotification_Count?.visibility = View.VISIBLE
                    chatNotification_Count?.text = snapshot.childrenCount.toString()
                }else{
                    ll_chatcount?.visibility =View.GONE
                    chatNotification_Count?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun addButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            add_image.visibility = View.VISIBLE
            add_text.visibility = View.VISIBLE
            add_video.visibility = View.VISIBLE
        } else {
            add_image.visibility = View.GONE
            add_text.visibility = View.GONE
            add_video.visibility = View.GONE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            add_text.startAnimation(fromBottom)
            add_image.startAnimation(fromBottom)
            add_video.startAnimation(fromBottom)
            create_post_fab.startAnimation(rotateOpen)
        } else {
            add_text.startAnimation(toBottom)
            add_image.startAnimation(toBottom)
            add_video.startAnimation(toBottom)
            create_post_fab.startAnimation(rotateClose)
            clearAnimation()
        }
    }

    private fun clearAnimation(){
        add_image.clearAnimation()
        add_text.clearAnimation()
        add_video.clearAnimation()
    }
}