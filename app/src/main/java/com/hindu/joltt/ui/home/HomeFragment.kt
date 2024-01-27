package com.hindu.joltt.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.FragmentHomeBinding
import com.hindu.joltt.Activity.AddPostActivity
import com.hindu.joltt.Activity.VideoUploadActivity
import com.hindu.joltt.Adapter.PostAdapter
import com.hindu.joltt.Model.DevMessageModel
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
    private var clicked = false



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


    private lateinit var postLayout_ll:LinearLayout
    private lateinit var emptyListPost:LinearLayout
    private lateinit var postRecyclerView:RecyclerView

    private lateinit var imin:AppCompatButton
    private lateinit var closeMessage_btn:AppCompatButton
    private lateinit var developerMessage_CV:CardView
    private lateinit var create_post_fab:FloatingActionButton

    private lateinit var add_image:FloatingActionButton
    private lateinit var add_video:FloatingActionButton
    private lateinit var add_text:FloatingActionButton

    private lateinit var onlyText_CV:RelativeLayout

    private lateinit var uploadTextBtn:ImageView

    private lateinit var addText_ET:EditText
    private lateinit var closeOnlyText:ImageView
    private lateinit var strike:ImageView

    private lateinit var welcome_screen:RelativeLayout
    private lateinit var dev_message_tv:TextView

    private lateinit var ll_chatcount:LinearLayout
    private lateinit var chatNotification_Count:TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        postLayout_ll = root.findViewById(R.id.postLayout_ll)
        emptyListPost = root.findViewById(R.id.emptyListPost)
        postRecyclerView = root.findViewById(R.id.postRecyclerView)
        imin = root.findViewById(R.id.imin)
        closeMessage_btn = root.findViewById(R.id.closeMessage_btn)
        developerMessage_CV = root.findViewById(R.id.developerMessage_CV)
        create_post_fab = root.findViewById(R.id.create_post_fab)
        add_image = root.findViewById(R.id.add_image)
        add_video = root.findViewById(R.id.add_video)
        add_text = root.findViewById(R.id.add_text)
        onlyText_CV = root.findViewById(R.id.onlyText_CV)
        uploadTextBtn = root.findViewById(R.id.uploadTextBtn)
        addText_ET = root.findViewById(R.id.addText_ET)
        closeOnlyText = root.findViewById(R.id.closeOnlyText)
        strike = root.findViewById(R.id.strike)
        welcome_screen = root.findViewById(R.id.welcome_screen)
        dev_message_tv = root.findViewById(R.id.dev_message_tv)
        ll_chatcount = root.findViewById(R.id.ll_chatcount)
        chatNotification_Count = root.findViewById(R.id.chatNotification_Count)





        CoroutineScope(Dispatchers.IO).launch {
            checkFirstVisit()
        }

        homeViewModel.postModel!!.observe(viewLifecycleOwner, Observer { postList ->

            if (postList.isNullOrEmpty()){
                postLayout_ll?.visibility = View.GONE
                emptyListPost.visibility = View.VISIBLE
            }else{
                postLayout_ll?.visibility = View.VISIBLE
                postRecyclerView.visibility = View.VISIBLE
                initView(root)
                postAdapter = context?.let { it1 -> PostAdapter(it1, postList,"Home") }
                recyclerView!!.adapter = postAdapter
                postAdapter!!.notifyDataSetChanged()
            }
        })


        CoroutineScope(Dispatchers.IO).launch() {
            launch { developerMessage() }
            launch { chatNotification() }
        }

        //first time visit
        imin.setOnClickListener {
            updateVisit(root)
        }

        //Close Developer Message
        closeMessage_btn.setOnClickListener {
            developerMessage_CV.visibility = View.GONE
        }

        //Create Post Floating Action Button
        create_post_fab.setOnClickListener {
            addButtonClicked()
        }

        add_image.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java))
            addButtonClicked()
        }

        add_video.setOnClickListener {
            startActivity(Intent(context, VideoUploadActivity::class.java))
            addButtonClicked()
        }

        add_text.setOnClickListener {
            onlyText_CV.visibility = View.VISIBLE
            addButtonClicked()
        }

        uploadTextBtn.setOnClickListener {
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
                onlyText_CV.visibility = View.GONE
                addText_ET.text.clear()
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                // Hide the soft input method
                inputMethodManager.hideSoftInputFromWindow(root.windowToken, 0)
            }

        }

        closeOnlyText.setOnClickListener {
            onlyText_CV.visibility = View.GONE
        }

        strike.setOnClickListener {

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

    private fun initView(root: View){
        recyclerView = root.findViewById(R.id.postRecyclerView) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        //loadUserImage(root)
        /*recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    create_post_fab.show()
                } else {
                    create_post_fab.hide()
                }
            }
        })*/


    }

    private suspend fun checkFirstVisit() {
        val dataRef = FirebaseDatabase
            .getInstance().reference.child("FirstVisit")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    welcome_screen?.visibility = View.VISIBLE
                    postLayout_ll?.visibility = View.GONE
                } else {
                    welcome_screen?.visibility = View.GONE
                    postLayout_ll?.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private suspend fun developerMessage() {
        val database = FirebaseDatabase.getInstance().reference.child("DevMessage")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    developerMessage_CV?.visibility = View.VISIBLE
                    val data = snapshot.getValue(DevMessageModel::class.java)
                    dev_message_tv?.text = data!!.message
                } else {
                    developerMessage_CV?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun chatNotification() {
        val data = FirebaseDatabase.getInstance().reference.child("ChatMessageCount")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        data.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    ll_chatcount?.visibility = View.VISIBLE
                    chatNotification_Count?.visibility = View.VISIBLE
                    chatNotification_Count?.text = snapshot.childrenCount.toString()
                } else {
                    ll_chatcount?.visibility = View.GONE
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

    private fun clearAnimation() {
        add_image.clearAnimation()
        add_text.clearAnimation()
        add_video.clearAnimation()
    }

    //Check Post Functions Post Functions


}