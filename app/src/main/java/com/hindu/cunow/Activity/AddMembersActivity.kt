package com.hindu.cunow.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Adapter.CircleMemberAdapter
import com.hindu.cunow.Adapter.JoinRequestAdapter
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_add_members.*


class AddMembersActivity : AppCompatActivity() {

    private lateinit var circleId:String
    private lateinit var admin: String
    private var userList: MutableList<UserModel>? = null
    private var circleMemberAdapter:CircleMemberAdapter? = null

    private var joinRequestAdapter:JoinRequestAdapter? = null
    private var requesterList:List<String>? = null
    private var uList:List<UserModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_members)

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()
        admin = intent.getStringExtra("admin").toString()

        joiningRequests.setOnClickListener {
            joiningRequests.visibility = View.GONE
            ll_close.visibility = View.VISIBLE
            addMembers_RV.visibility = View.GONE
            joinRequests_RV.visibility = View.VISIBLE
        }

        ll_close.setOnClickListener {
            joiningRequests.visibility = View.VISIBLE
            ll_close.visibility = View.GONE
            addMembers_RV.visibility = View.VISIBLE
            joinRequests_RV.visibility = View.GONE
        }

        val recyclerView:RecyclerView = findViewById(R.id.addMembers_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        userList = ArrayList()
        circleMemberAdapter = CircleMemberAdapter(this,userList as ArrayList<UserModel>, circleId, admin)
        recyclerView.adapter = circleMemberAdapter
        loadUsers()

        val recyclerView2:RecyclerView = findViewById(R.id.joinRequests_RV)
        val linearLayoutManager2 = LinearLayoutManager(this)
        linearLayoutManager2.reverseLayout = true
        recyclerView2.layoutManager = linearLayoutManager2
        linearLayoutManager2.stackFromEnd = true

        uList = ArrayList()
        joinRequestAdapter = JoinRequestAdapter(this,uList as ArrayList<UserModel>,circleId)
        recyclerView2.adapter = joinRequestAdapter
        requesterList = ArrayList()

        loadRequests()
        totalRequests()
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

    private fun loadRequests(){
        val database = FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId).child("JoinRequests")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (requesterList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children){
                        (requesterList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUser()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showUser(){
        val database = FirebaseDatabase.getInstance().reference
            .child("Users")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (uList as ArrayList<UserModel>).clear()

                for (snapshot in snapshot.children){
                    val user = snapshot.getValue(UserModel::class.java)

                    for (key in requesterList!!){
                        if (user!!.uid == key){
                            (uList as ArrayList<UserModel>).add(user)
                        }
                    }
                }
                joinRequestAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun totalRequests(){
        val data = FirebaseDatabase.getInstance().reference.child("Circle")
            .child(circleId)
            .child("JoinRequests")

        data.addValueEventListener(object :ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val totalRequests = snapshot.childrenCount.toInt()
                    if (totalRequests <=0){
                        totalJoinRequests.text = "(0)"
                    }else{
                        member_icon.visibility =View.GONE
                        notification_bell.visibility =View.VISIBLE
                        totalJoinRequests.setTextColor(resources.getColor(R.color.reddish))
                        totalJoinRequests.text = "("+snapshot.childrenCount.toString()+")"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}