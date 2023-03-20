package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Adapter.ProjectAdapter
import com.hindu.cunow.Model.ProjectModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectDetailsActivity : AppCompatActivity() {
    private var projectId = ""
    private var projectList:MutableList<ProjectModel>? = null
    private var projectAdapter:ProjectAdapter? = null
    val firebase = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)

        val intent = intent
        projectId = intent.getStringExtra("projectId").toString()
        CoroutineScope(Dispatchers.IO).launch {
            getProjectDetails()
        }
        /*val recyclerView:RecyclerView = findViewById(R.id.joinRequests_Projects_RV)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        projectList = ArrayList()
        projectAdapter = ProjectAdapter(this,projectList as ArrayList<ProjectModel>)
        recyclerView.adapter = projectAdapter
        CoroutineScope(Dispatchers.IO).launch {
            launch { loadRequests() }
        }*/
    }
    private fun getProjectDetails(){
        val ref = firebase
            .child("Projects")
            .child(projectId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(ProjectModel::class.java)
                    projectName_DetailActivity.text = data!!.projectName
                    projectDes_DetailActivity.text = data.projectDescription
                    Glide.with(this@ProjectDetailsActivity).load(data.projectImage).into(projectImage_detailsActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
    }

    /*private fun loadRequests(){
        val ref = firebase.child("Projects").child(projectId)
            .child("JoiningRequests")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    projectList!!.clear()
                    no_request_tv.visibility = View.GONE
                    joinRequests_Projects_RV.visibility = View.VISIBLE
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(ProjectModel::class.java)
                        projectList!!.add(data!!)
                    }
                    projectAdapter!!.notifyDataSetChanged()
                }else{
                    no_request_tv.visibility = View.VISIBLE
                    joinRequests_Projects_RV.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }*/

}