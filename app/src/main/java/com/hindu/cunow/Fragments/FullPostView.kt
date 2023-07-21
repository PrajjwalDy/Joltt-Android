package com.hindu.cunow.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Adapter.PublicPostAdapter
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.fragment_full_post_view.*
import kotlinx.android.synthetic.main.fragment_user_profiel.*

class FullPostView : Fragment() {
    private var recyclerView:RecyclerView? = null
    private var postAdapter:PostAdapter? = null
    private var mPost:MutableList<PostModel>? = null

    private lateinit var postId:String
    private lateinit var hasTag:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root:View = inflater.inflate(R.layout.fragment_full_post_view, container, false)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.postId = pref.getString("postId","none")!!
            this.hasTag = pref.getString("hashtag","none")!!
        }
        recyclerView = root.findViewById(R.id.fullPost_rv)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mPost = ArrayList()
        postAdapter = context?.let { PostAdapter(it,mPost as ArrayList<PostModel>) }
        recyclerView?.adapter = postAdapter

        if (postId == "no"){
            retrievePostFromTag()
        }else{
            returnPost(postId)
        }
        //retrievePost()

        return root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun returnPost(input:String){
        val array = FirebaseDatabase.getInstance().reference
            .child("Post")
            .orderByChild("postId")
            .startAt(input)
            .endAt(input+"\uf88f")
            array.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mPost!!.clear()
                    for (snapshot in snapshot.children){
                        val post = snapshot.getValue(PostModel::class.java)
                        if (post != null){
                            mPost?.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun retrievePostFromTag(){
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")
        postRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    mPost!!.clear()
                    for(snapshot in snapshot.children){
                        val post = snapshot.getValue(PostModel::class.java)
                        val caption = post!!.caption!!.trim{ it <= ' '}
                        val feedTags = caption.split(" ")
                        for (tag in feedTags) {
                            if (tag.startsWith("#")){
                                if (feedTags.contains(hasTag)) {
                                    mPost!!.add(post)
                                    break
                                }
                            }
                        }
                        postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}