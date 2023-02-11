package com.hindu.cunow.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Model.MyAppModel
import com.hindu.cunow.Model.ProjectModel
import com.hindu.cunow.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAppAdapter(private val mContext:Context,
                   private val mApp:List<MyAppModel>):RecyclerView.Adapter<MyAppAdapter.ViewHolder>() {

                       inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
                           private val projectImage:ImageView= itemView.findViewById(R.id.projectImage_myapp) as ImageView
                           private val projectName: TextView = itemView.findViewById(R.id.projectName_myapp) as TextView
                           private val statusButton: AppCompatButton = itemView.findViewById(R.id.application_status_btn) as AppCompatButton

                           fun bind(list:MyAppModel){
                               CoroutineScope(Dispatchers.IO).launch {
                                   getProjectDetails(projectName,projectImage,list.projectId!!)
                               }
                           }
                       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.myapplication_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mApp.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    private fun getProjectDetails(projectName:TextView,projectImage:ImageView,projectId:String){
        val ref = FirebaseDatabase.getInstance().reference.child("Projects")
            .child(projectId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(ProjectModel::class.java)
                    projectName.text = data!!.projectName
                    Glide.with(mContext).load(data.projectImage).into(projectImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
    }

    private fun applicationStatus(){
        val ref = FirebaseDatabase.getInstance().reference
            .child("Project")
    }
}