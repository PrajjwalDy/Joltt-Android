package com.hindu.cunow.Fragments.AboutMe.UserPosts

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.NotificationAdapter
import com.hindu.cunow.Adapter.PostAdapter
import com.hindu.cunow.Adapter.PostGridAdapter
import com.hindu.cunow.Model.NotificationModel
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.R
import com.hindu.cunow.ui.notifications.NotificationsViewModel
import kotlinx.android.synthetic.main.my_posts_fragemt_fragment.view.*
import kotlinx.android.synthetic.main.user_posts_fragment.view.*
import java.util.*
import kotlin.collections.ArrayList

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