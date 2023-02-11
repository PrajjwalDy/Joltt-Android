package com.hindu.cunow.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.ProjectModel
import com.hindu.cunow.R
import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectDetailsActivity : AppCompatActivity() {
    private var projectId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)

        val intent = intent
        projectId = intent.getStringExtra("projectId").toString()
        CoroutineScope(Dispatchers.IO).launch {
            getProjectDetails()
        }


    }
    private fun getProjectDetails(){
        val ref = FirebaseDatabase.getInstance().reference
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
}