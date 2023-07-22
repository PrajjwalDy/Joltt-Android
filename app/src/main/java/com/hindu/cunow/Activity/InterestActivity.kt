package com.hindu.cunow.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.InterestAdapter
import com.hindu.cunow.MainActivity
import com.hindu.cunow.Model.InterestModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_interest.*

class InterestActivity : AppCompatActivity() {
    private var firebaseUser:FirebaseUser? =null
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private var interestList:MutableList<InterestModel>? = null
    private var interestAdapter:InterestAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView:RecyclerView = findViewById(R.id.interest_RV)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this,2)
        recyclerView.layoutManager= linearLayoutManager
        interestList = ArrayList()
        interestAdapter = InterestAdapter(this,interestList as ArrayList)
        recyclerView.adapter = interestAdapter
        loadData()


        proceed_interest.setOnClickListener { view->
            checkInterest(view)
        }
    }

    private fun loadData(){
        val dbRef = FirebaseDatabase.getInstance().reference.child("interests")
        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    interestList!!.clear()
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(InterestModel::class.java)
                        interestList!!.add(data!!)
                    }
                    interestAdapter!!.notifyDataSetChanged()
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun checkInterest(view: View){
        val dbRef = FirebaseDatabase.getInstance().reference.child("UserInterests")
            dbRef.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(firebaseUser!!.uid)){
                        val intent = Intent(this@InterestActivity, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Snackbar.make(view,"Please choose your interest to proceed",1000).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}