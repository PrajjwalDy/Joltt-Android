package com.hindu.joltt.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.ShowUserAdapter
import com.hindu.joltt.Model.UserModel
import kotlinx.android.synthetic.main.show_user_fragment.noFriendsLayout
import kotlinx.android.synthetic.main.show_user_fragment.showUserFrag_RV
import kotlinx.android.synthetic.main.show_user_fragment.view.noFriendsLayout
import kotlinx.android.synthetic.main.show_user_fragment.view.showUserFrag_RV
import kotlinx.android.synthetic.main.show_user_fragment.view.title_showUsers

class ShowUserFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var userList: List<UserModel>? = null
    private var showUserList: List<String>? = null
    private var showUserAdapter: ShowUserAdapter? = null

    private var uid = ""
    private var title = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.show_user_fragment, container, false)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.uid = pref.getString("uid", "none")!!
            this.title = pref.getString("title", "none")!!
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.showUserFrag_RV) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        userList = ArrayList()
        showUserAdapter =
            context?.let { ShowUserAdapter(it, userList as ArrayList<UserModel>, title) }
        recyclerView.adapter = showUserAdapter
        showUserList = ArrayList()

        when (title) {
            "Followers" -> getFollowers()
            "Following" -> getFollowing()
            "Chat" -> getFollowers()
        }

        root.title_showUsers.text = title
        if (recyclerView.adapter?.itemCount == 0) {
            root.noFriendsLayout.visibility = View.VISIBLE
            root.showUserFrag_RV.visibility = View.GONE
        } else {
            root.noFriendsLayout.visibility = View.GONE
            root.showUserFrag_RV.visibility = View.VISIBLE
        }

        return root
    }

    private fun getFollowers() {
        val userData = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(uid)
            .child("Followers")

        userData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        (showUserList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getFollowing() {
        val userData = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(uid)
            .child("Following")

        userData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        (showUserList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showUser() {
        val userData = FirebaseDatabase.getInstance().reference
            .child("Users")
        userData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (userList as ArrayList<UserModel>).clear()
                    for (snapshot in snapshot.children) {
                        val user = snapshot.getValue(UserModel::class.java)
                        for (key in showUserList!!) {
                            if (user!!.uid == key) {
                                (userList as ArrayList<UserModel>).add(user)
                            }
                        }
                    }
                    showUserAdapter!!.notifyDataSetChanged()

                    if (userList.isNullOrEmpty()) {
                        noFriendsLayout?.visibility = View.VISIBLE
                        showUserFrag_RV?.visibility = View.GONE
                    } else {
                        noFriendsLayout?.visibility = View.GONE
                        showUserFrag_RV?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}