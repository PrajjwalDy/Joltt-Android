package com.hindu.cunow.Fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
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
import com.hindu.cunow.Adapter.ShowUserAdapter
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import com.hindu.cunow.databinding.ShowUserFragmentBinding
import kotlinx.android.synthetic.main.show_user_fragment.view.*

class ShowUserFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var userList:List<UserModel>? = null
    private var showUserList:List<String>? = null
    private var showUserAdapter:ShowUserAdapter? =null

    private var uid = ""
    private var title = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.show_user_fragment, container, false)

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.uid = pref.getString("uid","none")!!
            this.title = pref.getString("title","none")!!
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.showUserFrag_RV) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        userList = ArrayList()
         showUserAdapter = context?.let { ShowUserAdapter(it,userList as ArrayList<UserModel>) }
        recyclerView.adapter = showUserAdapter
        showUserList = ArrayList()

        when(title){
            "Followers"->getFollowers()
            "Following"->getFollowing()
        }

        root.title_showUsers.text = title

        return root
    }

    private fun getFollowers(){
        val userData = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(uid)
            .child("Followers")

        userData.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
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

    private fun getFollowing(){
        val userData = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(uid)
            .child("Following")

        userData.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
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
        userData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (userList as ArrayList<UserModel>).clear()
                    for (snapshot in snapshot.children){
                        val user = snapshot.getValue(UserModel::class.java)
                        for (key in showUserList!!){
                            if (user!!.uid == key){
                                (userList as ArrayList<UserModel>).add(user)
                            }
                        }
                    }
                    showUserAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}