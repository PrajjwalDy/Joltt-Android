package com.hindu.cunow.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.CircleMemberAdapter
import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_add_members.*
import kotlinx.android.synthetic.main.activity_circle_details.*
import kotlinx.android.synthetic.main.activity_circle_details.view.*
import kotlinx.android.synthetic.main.admin_option_cirlce.view.*
import kotlinx.android.synthetic.main.circle_members_layout.*

class CircleDetailsActivity : AppCompatActivity() {

    private lateinit var circleId:String
    private lateinit var admin: String
    private lateinit var firebaseUser: FirebaseUser
    private var userList:List<UserModel>? = null
    private var memberList: List<String>? = null
    private var circleMemberAdapter:CircleMemberAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_details)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val intent = intent
        circleId = intent.getStringExtra("circleId").toString()
        admin = intent.getStringExtra("admin").toString()

        if (admin == FirebaseAuth.getInstance().currentUser!!.uid){
            moreOptionCircle.visibility = View.VISIBLE
            addMembers.visibility = View.VISIBLE
        }else{
            moreOptionCircle.visibility = View.GONE
            addMembers.visibility = View.GONE
            notificationDot.visibility = View.GONE
        }

        moreOptionCircle.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.admin_option_cirlce, null)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertDialog = dialogBuilder.show()

            dialogView.circlePermission.setOnClickListener {
                val intent = Intent(this,CirclePermissionActivity::class.java)
                intent.putExtra("circleId",circleId)
                intent.putExtra("admin",admin)
                startActivity(intent)
                alertDialog.dismiss()
            }

            dialogView.deleteCircle.setOnClickListener {
                FirebaseDatabase.getInstance().reference
                    .child("Circle")
                    .child(circleId)
                    .removeValue()
                alertDialog.dismiss()
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.circleMember_details_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.stackFromEnd = true

        userList = ArrayList()
        circleMemberAdapter = CircleMemberAdapter(this,userList as ArrayList<UserModel>, circleId, admin)
        recyclerView.adapter = circleMemberAdapter

        memberList = ArrayList()

        addMembers.setOnClickListener {
            val intent = Intent(this,AddMembersActivity::class.java)
            intent.putExtra("circleId",circleId)
            intent.putExtra("admin",admin)
            startActivity(intent)
        }

        loadMembers()
        getCircleDetails()
        getAdmin()
        totalRequests()
    }



    private fun getCircleDetails(){
        val dataRef = FirebaseDatabase.getInstance().reference.child("Circle").child(circleId)

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(CircleModel::class.java)
                    Glide.with(this@CircleDetailsActivity).load(data!!.icon).into(circleIcon_details)
                    circle_name_details.text = data.circleName
                    circle_description_details.text = data.circle_description
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getAdmin(){

        val userData = FirebaseDatabase.getInstance().reference.child("Users").child(admin)
        userData.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    Glide.with(this@CircleDetailsActivity).load(user!!.profileImage).into(profileImage_circleAdmin)
                    fullName_admin.text = user.fullName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadMembers(){
        val database = FirebaseDatabase.getInstance().reference
            .child("Circle")
            .child(circleId).child("Members")

        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (memberList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children){
                        (memberList as ArrayList<String>).add(snapshot.key!!)
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
        val database = FirebaseDatabase.getInstance().reference.child("Users")
        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                (userList as ArrayList<UserModel>).clear()

                for (snapshot in snapshot.children){
                    val user = snapshot.getValue(UserModel::class.java)

                    for (id in memberList!!){
                            if (user!!.uid == id){
                                (userList as ArrayList<UserModel>).add(user)
                            }
                    }
                }

                circleMemberAdapter!!.notifyDataSetChanged()
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
                    if (totalRequests >0 && admin == firebaseUser.uid){
                        notificationDot.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}