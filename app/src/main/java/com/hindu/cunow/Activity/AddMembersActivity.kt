package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Adapter.CircleMemberAdapter
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R

class AddMembersActivity : AppCompatActivity() {

    private lateinit var circleId:String
    private lateinit var admin: String
    private var userList: MutableList<UserModel>? = null
    private var circleMemberAdapter:CircleMemberAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_members)

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()
        admin = intent.getStringExtra("admin").toString()


        val recyclerView:RecyclerView = findViewById(R.id.addMembers_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        userList = ArrayList()
        circleMemberAdapter = CircleMemberAdapter(this,userList as ArrayList<UserModel>, circleId, admin)
        recyclerView.adapter = circleMemberAdapter
        loadUsers()
    }

    private fun loadUsers(){
        val userData = FirebaseDatabase.getInstance().reference
            .child("Users")
            userData.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        userList!!.clear()
                        for (snapshot in snapshot.children){
                            val user = snapshot.getValue(UserModel::class.java)
                            userList!!.add(user!!)
                        }
                        circleMemberAdapter!!.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}