package com.hindu.joltt.Fragments.AboutMe.UserPosts

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.PostAdapter
import com.hindu.joltt.Adapter.PostGridAdapter
import com.hindu.joltt.Model.PostModel
import kotlinx.android.synthetic.main.user_posts_fragment.view.postVertical_user
import kotlinx.android.synthetic.main.user_posts_fragment.view.userPostRV_grid

class UserPostsFragment : Fragment() {

    private lateinit var profileId:String

    private var postList: List<PostModel>? = null
    private var postAdapter: PostAdapter? = null
    private var postGridAdapter: PostGridAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View= inflater.inflate(R.layout.user_posts_fragment, container, false)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("uid","none")!!
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.userPostsRV) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it,postList as ArrayList<PostModel>) }
        recyclerView.adapter = postAdapter

        //Posts in Grid View
        val recyclerViewGrid: RecyclerView = root.findViewById(R.id.userPostRV_grid) as RecyclerView
        recyclerViewGrid.setHasFixedSize(true)
        val linearLayoutManagerGrid: LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewGrid.layoutManager = linearLayoutManagerGrid

        postList = ArrayList()
        postGridAdapter = context?.let { PostGridAdapter(it,postList as ArrayList<PostModel>) }
        recyclerViewGrid.adapter = postGridAdapter
        retrievePosts()

        root.postVertical_user.setOnClickListener {
            root.userPostRV_grid.visibility = View.GONE
        }

        return root
    }

    private fun retrievePosts() {
        val progressDialog = context?.let { Dialog(it) }
        progressDialog!!.setContentView(R.layout.profile_dropdown_menu)
        progressDialog.show()
        val dataRef = FirebaseDatabase.getInstance().reference.child("Post")

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                    for (snapshot in snapshot.children){

                        val data = snapshot.getValue(PostModel::class.java)

                        if (data!!.publisher == profileId){
                            (postList as ArrayList<PostModel>).add(data)

                            postAdapter!!.notifyDataSetChanged()
                            postGridAdapter!!.notifyDataSetChanged()
                        }

                    }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        progressDialog.dismiss()
    }


}